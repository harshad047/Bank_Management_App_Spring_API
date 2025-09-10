package com.tss.bank.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.bank.dto.request.TransferConfirmationRequest;
import com.tss.bank.dto.request.TransferRequest;
import com.tss.bank.dto.response.TransferConfirmationResponse;
import com.tss.bank.dto.response.TransferResponse;
import com.tss.bank.entity.Account;
import com.tss.bank.entity.Transfer;
import com.tss.bank.exception.TransferApiException;
import com.tss.bank.repository.AccountRepository;
import com.tss.bank.repository.TransferRepository;
import com.tss.bank.service.AccountService;
import com.tss.bank.service.MappingService;
import com.tss.bank.service.TransferService;

@Service
@Transactional
public class TransferServiceImpl implements TransferService {

    @Autowired
    private TransferRepository transferRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private MappingService mappingService;
    
    private static final BigDecimal DAILY_TRANSFER_LIMIT = new BigDecimal("100000.00");
    private static final BigDecimal PER_TRANSFER_LIMIT = new BigDecimal("50000.00");
    private static final BigDecimal MONTHLY_TRANSFER_LIMIT = new BigDecimal("500000.00");
    
    // In-memory storage for OTPs (in production, use Redis or database)
    private final ConcurrentHashMap<Integer, String> transferOTPs = new ConcurrentHashMap<>();

    @Override
    public TransferResponse initiateTransfer(TransferRequest request) {
        if (!validateTransfer(request)) {
            throw new TransferApiException("Invalid transfer request");
        }
        
        Account fromAccount = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new TransferApiException("Source account not found"));
        
        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new TransferApiException("Destination account not found"));
        
        // Validate transfer limits
        if (!validateTransferLimits(request.getFromAccountId(), request.getAmount())) {
            throw new TransferApiException("Transfer amount exceeds per-transaction limit");
        }
        
        if (!validateDailyTransferLimit(request.getFromAccountId(), request.getAmount())) {
            throw new TransferApiException("Daily transfer limit exceeded");
        }
        
        // Process transfer directly without OTP for testing
        processTransfer(fromAccount.getAccountId(), toAccount.getAccountId(), 
                       request.getAmount(), request.getDescription());
        
        // Create transfer record with SUCCESS status
        Transfer transfer = Transfer.builder()
                .fromAccountId(fromAccount.getAccountId())
                .toAccountId(toAccount.getAccountId())
                .toAccountNumber(request.getToAccountNumber())
                .amount(request.getAmount())
                .description(request.getDescription())
                .transferTime(new Date())
                .transferDate(new Date())
                .status(Transfer.Status.COMPLETED)
                .build();
        
        Transfer savedTransfer = transferRepository.save(transfer);
        
        return mappingService.map(savedTransfer, TransferResponse.class);
    }

    @Override
    public TransferConfirmationResponse confirmTransfer(TransferConfirmationRequest request) {
        Transfer transfer = transferRepository.findById(request.getTransferId())
                .orElseThrow(() -> new TransferApiException("Transfer not found"));
        
        // Verify OTP
        if (!verifyTransferOTP(request.getTransferId(), request.getOtp())) {
            throw new TransferApiException("Invalid OTP");
        }
        
        // Validate transaction password (simplified validation)
        Account fromAccount = accountRepository.findById(transfer.getFromAccountId())
                .orElseThrow(() -> new TransferApiException("Source account not found"));
        
        if (!validateTransactionPassword(fromAccount.getUser().getUserId(), request.getTransactionPassword())) {
            throw new TransferApiException("Invalid transaction password");
        }
        
        // Process the transfer
        processTransfer(transfer.getFromAccountId(), transfer.getToAccountId(), 
                       transfer.getAmount(), transfer.getDescription());
        
        // Generate transaction reference
        String transactionReference = generateTransferReference();
        
        Account toAccount = accountRepository.findById(transfer.getToAccountId())
                .orElseThrow(() -> new TransferApiException("Destination account not found"));
        
        return TransferConfirmationResponse.builder()
                .transferId(transfer.getTransferId())
                .transactionReference(transactionReference)
                .fromAccountId(transfer.getFromAccountId())
                .fromAccountNumber(fromAccount.getAccountNumber())
                .toAccountNumber(toAccount.getAccountNumber())
                .beneficiaryName(toAccount.getUser().getUsername())
                .amount(transfer.getAmount())
                .charges(BigDecimal.ZERO) // No charges for now
                .totalAmount(transfer.getAmount())
                .transferTime(transfer.getTransferTime())
                .status("SUCCESS")
                .message("Transfer completed successfully")
                .build();
    }

    @Override
    public TransferResponse getTransferDetails(Integer transferId) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new TransferApiException("Transfer not found with ID: " + transferId));
        return mappingService.map(transfer, TransferResponse.class);
    }

    @Override
    public boolean validateTransfer(TransferRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        // Check if source account has sufficient balance
        return accountService.hasMinimumBalance(request.getFromAccountId(), request.getAmount());
    }

    @Override
    public boolean validateBeneficiaryAccount(String accountNumber, String ifscCode) {
        // In real implementation, validate with external bank APIs
        return accountRepository.existsByAccountNumber(accountNumber);
    }

    @Override
    public boolean validateTransferLimits(Integer fromAccountId, BigDecimal amount) {
        return amount.compareTo(PER_TRANSFER_LIMIT) <= 0;
    }

    @Override
    public boolean validateDailyTransferLimit(Integer fromAccountId, BigDecimal amount) {
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        BigDecimal dailyTotal = getDailyTransferAmount(fromAccountId, today);
        BigDecimal newTotal = dailyTotal.add(amount);
        
        return newTotal.compareTo(DAILY_TRANSFER_LIMIT) <= 0;
    }

    @Override
    public String generateTransferReference() {
        return "TXN" + System.currentTimeMillis();
    }

    @Override
    @Transactional
    public void processTransfer(Integer fromAccountId, Integer toAccountId, BigDecimal amount, String description) {
        // Debit from source account
        accountService.debitAmount(fromAccountId, amount, "Transfer: " + description);
        
        // Credit to destination account
        accountService.creditAmount(toAccountId, amount, "Transfer received: " + description);
    }

    @Override
    public void reverseTransfer(Integer transferId, String reason) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new TransferApiException("Transfer not found"));
        
        // Reverse the transfer
        accountService.debitAmount(transfer.getToAccountId(), transfer.getAmount(), "Transfer reversal: " + reason);
        accountService.creditAmount(transfer.getFromAccountId(), transfer.getAmount(), "Transfer reversal: " + reason);
    }

    @Override
    public String generateTransferOTP(Integer transferId) {
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(1000000));
        transferOTPs.put(transferId, otp);
        
        // In real implementation, send OTP via SMS/Email
        return otp;
    }

    @Override
    public boolean verifyTransferOTP(Integer transferId, String otp) {
        String storedOtp = transferOTPs.get(transferId);
        if (storedOtp != null && storedOtp.equals(otp)) {
            transferOTPs.remove(transferId); // Remove OTP after verification
            return true;
        }
        return false;
    }

    @Override
    public boolean validateTransactionPassword(Integer userId, String transactionPassword) {
        // Simplified validation - in real implementation, check against stored transaction password
        return transactionPassword != null && transactionPassword.length() >= 4;
    }

    @Override
    public List<TransferResponse> getTransferHistory(Integer accountId) {
        List<Transfer> transfers = transferRepository.findByFromAccountIdOrToAccountIdOrderByTransferTimeDesc(
                accountId, accountId);
        return mappingService.mapList(transfers, TransferResponse.class);
    }

    @Override
    public Page<TransferResponse> getTransferHistoryPaginated(Integer accountId, Pageable pageable) {
        Page<Transfer> transfers = transferRepository.findByFromAccountIdOrToAccountId(
                accountId, accountId, pageable);
        return transfers.map(transfer -> mappingService.map(transfer, TransferResponse.class));
    }

    @Override
    public List<TransferResponse> getTransfersByDateRange(Integer accountId, Date fromDate, Date toDate) {
        List<Transfer> transfers = transferRepository.findByAccountIdAndTransferTimeBetween(
                accountId, fromDate, toDate);
        return mappingService.mapList(transfers, TransferResponse.class);
    }

    @Override
    public BigDecimal getTotalTransferredAmount(Integer accountId, Date fromDate, Date toDate) {
        return transferRepository.getTotalTransferredAmountByAccountAndDateRange(
                accountId, fromDate, toDate);
    }

    @Override
    public BigDecimal getDailyTransferAmount(Integer accountId, Date date) {
        Date startOfDay = Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                .plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        return transferRepository.getTotalTransferredAmountByAccountAndDateRange(
                accountId, startOfDay, endOfDay);
    }

    @Override
    public long getTransferCount(Integer accountId, Date fromDate, Date toDate) {
        return transferRepository.countByAccountIdAndTransferTimeBetween(accountId, fromDate, toDate);
    }

    @Override
    public BigDecimal getDailyTransferLimit() {
        return DAILY_TRANSFER_LIMIT;
    }

    @Override
    public BigDecimal getPerTransferLimit() {
        return PER_TRANSFER_LIMIT;
    }

    @Override
    public BigDecimal getMonthlyTransferLimit() {
        return MONTHLY_TRANSFER_LIMIT;
    }

    @Override
    public Page<TransferResponse> getAllTransfers(Pageable pageable) {
        Page<Transfer> transfers = transferRepository.findAll(pageable);
        return transfers.map(transfer -> mappingService.map(transfer, TransferResponse.class));
    }

    @Override
    public List<TransferResponse> getHighValueTransfers(BigDecimal threshold) {
        List<Transfer> transfers = transferRepository.findByAmountGreaterThan(threshold);
        return mappingService.mapList(transfers, TransferResponse.class);
    }

    @Override
    public List<TransferResponse> getPendingTransfers() {
        List<Transfer> transfers = transferRepository.findByStatus(Transfer.Status.PENDING);
        return mappingService.mapList(transfers, TransferResponse.class);
    }

    @Override
    public List<TransferResponse> getFailedTransfers() {
        List<Transfer> transfers = transferRepository.findByStatus(Transfer.Status.FAILED);
        return mappingService.mapList(transfers, TransferResponse.class);
    }

    @Override
    public String generateOTP(Integer transferId) {
        return generateTransferOTP(transferId);
    }

    @Override
    public boolean verifyOTP(Integer transferId, String otp) {
        return verifyTransferOTP(transferId, otp);
    }

    @Override
    public TransferResponse processTransfer(Integer transferId) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new TransferApiException("Transfer not found"));
        
        // Process the transfer
        processTransfer(transfer.getFromAccountId(), transfer.getToAccountId(), 
                       transfer.getAmount(), transfer.getDescription());
        
        // Update transfer status
        transfer.setStatus(Transfer.Status.COMPLETED);
        transfer.setTransactionReference(generateTransferReference());
        Transfer updatedTransfer = transferRepository.save(transfer);
        
        return mappingService.map(updatedTransfer, TransferResponse.class);
    }

    @Override
    public BigDecimal getMonthlyTransferAmount(Integer accountId, Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date startOfMonth = Date.from(localDate.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfMonth = Date.from(localDate.withDayOfMonth(localDate.lengthOfMonth())
                .plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        return transferRepository.getTotalTransferredAmountByAccountAndDateRange(
                accountId, startOfMonth, endOfMonth);
    }

    @Override
    public boolean isTransferAllowed(Integer fromAccountId, BigDecimal amount) {
        return validateTransferLimits(fromAccountId, amount) && 
               validateDailyTransferLimit(fromAccountId, amount);
    }
}
