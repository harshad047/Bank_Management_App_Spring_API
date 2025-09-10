package com.tss.bank.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.bank.dto.request.TransactionRequest;
import com.tss.bank.dto.request.TransactionHistoryRequest;
import com.tss.bank.dto.response.TransactionResponse;
import com.tss.bank.dto.response.AccountStatementResponse;
import com.tss.bank.entity.Account;
import com.tss.bank.entity.Transaction;
import com.tss.bank.exception.AccountApiException;
import com.tss.bank.exception.TransactionApiException;
import com.tss.bank.repository.AccountRepository;
import com.tss.bank.repository.TransactionRepository;
import com.tss.bank.service.TransactionService;
import com.tss.bank.service.MappingService;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private MappingService mappingService;
    
    // Transaction limits
    private static final BigDecimal DAILY_WITHDRAWAL_LIMIT = new BigDecimal("50000");
    private static final BigDecimal PER_TRANSACTION_LIMIT = new BigDecimal("25000");
    private static final BigDecimal MINIMUM_TRANSACTION_AMOUNT = new BigDecimal("1");
    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("100000");
    private static final BigDecimal SUSPICIOUS_THRESHOLD = new BigDecimal("50000");

    @Override
    public TransactionResponse processDeposit(TransactionRequest request) {
        if (request.getTxnType() != Transaction.TxnType.CREDIT) {
            throw new TransactionApiException("Invalid transaction type for deposit");
        }
        
        // Validate transaction
        if (!validateTransaction(request)) {
            throw new TransactionApiException("Transaction validation failed");
        }
        
        // Get account
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountApiException("Account not found with ID: " + request.getAccountId()));
        
        // Update account balance
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);
        
        // Record transaction
        recordTransaction(request.getAccountId(), Transaction.TxnType.CREDIT, 
                         request.getAmount(), request.getDescription(), request.getChannel());
        
        // Get the latest transaction for response
        Transaction transaction = transactionRepository.findTopByAccountAccountIdOrderByTxnTimeDesc(request.getAccountId())
                .orElseThrow(() -> new TransactionApiException("Failed to retrieve transaction"));
        
        return mappingService.map(transaction, TransactionResponse.class);
    }

    @Override
    public TransactionResponse processWithdrawal(TransactionRequest request) {
        if (request.getTxnType() != Transaction.TxnType.DEBIT) {
            throw new TransactionApiException("Invalid transaction type for withdrawal");
        }
        
        // Validate transaction
        if (!validateTransaction(request)) {
            throw new TransactionApiException("Transaction validation failed");
        }
        
        // Additional validations for withdrawal
        if (!validateWithdrawalLimit(request.getAccountId(), request.getAmount())) {
            throw new TransactionApiException("Withdrawal amount exceeds per-transaction limit");
        }
        
        if (!validateDailyLimit(request.getAccountId(), request.getAmount())) {
            throw new TransactionApiException("Withdrawal amount exceeds daily limit");
        }
        
        // Get account
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountApiException("Account not found with ID: " + request.getAccountId()));
        
        // Check sufficient balance
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new TransactionApiException("Insufficient balance");
        }
        
        // Update account balance
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);
        
        // Record transaction
        recordTransaction(request.getAccountId(), Transaction.TxnType.DEBIT, 
                         request.getAmount(), request.getDescription(), request.getChannel());
        
        // Get the latest transaction for response
        Transaction transaction = transactionRepository.findTopByAccountAccountIdOrderByTxnTimeDesc(request.getAccountId())
                .orElseThrow(() -> new TransactionApiException("Failed to retrieve transaction"));
        
        return mappingService.map(transaction, TransactionResponse.class);
    }

    @Override
    public TransactionResponse getTransactionDetails(Integer txnId) {
        Transaction transaction = transactionRepository.findById(txnId)
                .orElseThrow(() -> new TransactionApiException("Transaction not found with ID: " + txnId));
        return mappingService.map(transaction, TransactionResponse.class);
    }

    @Override
    public Page<TransactionResponse> getTransactionHistory(TransactionHistoryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        
        Page<Transaction> transactionPage;
        
        if (request.getFromDate() != null && request.getToDate() != null) {
            transactionPage = transactionRepository.findByAccountIdAndTxnTimeBetween(
                    request.getAccountId(), request.getFromDate(), request.getToDate(), pageable);
        } else {
            transactionPage = transactionRepository.findByAccountAccountIdOrderByTxnTimeDesc(
                    request.getAccountId(), pageable);
        }
        
        List<TransactionResponse> responses = transactionPage.getContent().stream()
                .map(txn -> mappingService.map(txn, TransactionResponse.class))
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, transactionPage.getTotalElements());
    }

    @Override
    public List<TransactionResponse> getAccountTransactions(Integer accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountAccountIdOrderByTxnTimeDesc(accountId);
        return transactions.stream()
                .map(txn -> mappingService.map(txn, TransactionResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getUserTransactions(Integer userId) {
        List<Transaction> transactions = transactionRepository.findByAccountUserUserIdOrderByTxnTimeDesc(userId);
        return transactions.stream()
                .map(txn -> mappingService.map(txn, TransactionResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public AccountStatementResponse generateAccountStatement(Integer accountId, Date fromDate, Date toDate) {
        // Get account details
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountApiException("Account not found with ID: " + accountId));
        
        // Get transactions for the period
        List<Transaction> transactions = transactionRepository.findByAccountIdAndTxnTimeBetweenOrderByTxnTimeAsc(
                accountId, fromDate, toDate);
        
        // Calculate opening balance (balance before fromDate)
        BigDecimal openingBalance = calculateOpeningBalance(accountId, fromDate);
        
        // Convert transactions to response DTOs
        List<TransactionResponse> transactionResponses = transactions.stream()
                .map(txn -> mappingService.map(txn, TransactionResponse.class))
                .collect(Collectors.toList());
        
        return AccountStatementResponse.builder()
                .accountId(accountId)
                .accountNumber(account.getAccountNumber())
                .accountHolderName(account.getUser().getUsername())
                .fromDate(fromDate)
                .toDate(toDate)
                .openingBalance(openingBalance)
                .closingBalance(account.getBalance())
                .transactions(transactionResponses)
                .statementFormat("PDF")
                .generatedAt(new Date())
                .build();
    }

    @Override
    public boolean validateTransaction(TransactionRequest request) {
        // Basic validations
        if (request.getAmount().compareTo(MINIMUM_TRANSACTION_AMOUNT) < 0) {
            return false;
        }
        
        // Check if account exists and is active
        Account account = accountRepository.findById(request.getAccountId()).orElse(null);
        if (account == null || account.getStatus() != Account.Status.ACTIVE) {
            return false;
        }
        
        return true;
    }

    @Override
    public boolean validateWithdrawalLimit(Integer accountId, BigDecimal amount) {
        return amount.compareTo(PER_TRANSACTION_LIMIT) <= 0;
    }

    @Override
    public boolean validateDailyLimit(Integer accountId, BigDecimal amount) {
        Date today = getTodayStart();
        Date tomorrow = getTodayEnd();
        
        BigDecimal todayWithdrawals = getTotalDebitAmount(accountId, today, tomorrow);
        BigDecimal totalWithdrawal = todayWithdrawals.add(amount);
        
        return totalWithdrawal.compareTo(DAILY_WITHDRAWAL_LIMIT) <= 0;
    }

    @Override
    public void recordTransaction(Integer accountId, Transaction.TxnType type, BigDecimal amount, 
                                 String description, Transaction.Channel channel) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountApiException("Account not found with ID: " + accountId));
        
        Transaction transaction = Transaction.builder()
                .account(account)
                .user(account.getUser())
                .txnType(type)
                .amount(amount)
                .description(description)
                .txnTime(new Date())
                .balanceAfter(account.getBalance())
                .channel(channel)
                .createdAt(new Date())
                .build();
        
        transactionRepository.save(transaction);
    }

    @Override
    public void reverseTransaction(Integer txnId, String reason) {
        Transaction originalTxn = transactionRepository.findById(txnId)
                .orElseThrow(() -> new TransactionApiException("Transaction not found with ID: " + txnId));
        
        // Create reverse transaction
        Transaction.TxnType reverseType = originalTxn.getTxnType() == Transaction.TxnType.DEBIT ? 
                Transaction.TxnType.CREDIT : Transaction.TxnType.DEBIT;
        
        Account account = originalTxn.getAccount();
        
        // Update account balance
        if (reverseType == Transaction.TxnType.CREDIT) {
            account.setBalance(account.getBalance().add(originalTxn.getAmount()));
        } else {
            account.setBalance(account.getBalance().subtract(originalTxn.getAmount()));
        }
        accountRepository.save(account);
        
        // Record reverse transaction
        recordTransaction(account.getAccountId(), reverseType, originalTxn.getAmount(), 
                         "REVERSAL: " + reason, Transaction.Channel.ONLINE);
    }

    @Override
    public BigDecimal getTotalDebitAmount(Integer accountId, Date fromDate, Date toDate) {
        BigDecimal total = transactionRepository.getTotalAmountByAccountAndTypeAndDateRange(
                accountId, Transaction.TxnType.DEBIT, fromDate, toDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalCreditAmount(Integer accountId, Date fromDate, Date toDate) {
        BigDecimal total = transactionRepository.getTotalAmountByAccountAndTypeAndDateRange(
                accountId, Transaction.TxnType.CREDIT, fromDate, toDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getDailyTransactionAmount(Integer accountId, Date date) {
        Date startOfDay = getTodayStart(date);
        Date endOfDay = getTodayEnd(date);
        
        BigDecimal debitAmount = getTotalDebitAmount(accountId, startOfDay, endOfDay);
        BigDecimal creditAmount = getTotalCreditAmount(accountId, startOfDay, endOfDay);
        
        return debitAmount.add(creditAmount);
    }

    @Override
    public long getTransactionCount(Integer accountId, Date fromDate, Date toDate) {
        return transactionRepository.countByAccountAndDateRange(accountId, fromDate, toDate);
    }

    @Override
    public BigDecimal getDailyWithdrawalLimit() {
        return DAILY_WITHDRAWAL_LIMIT;
    }

    @Override
    public BigDecimal getPerTransactionLimit() {
        return PER_TRANSACTION_LIMIT;
    }

    @Override
    public boolean isTransactionAllowed(Integer accountId, BigDecimal amount) {
        return validateWithdrawalLimit(accountId, amount) && validateDailyLimit(accountId, amount);
    }

    @Override
    public Page<TransactionResponse> getAllTransactions(Pageable pageable) {
        Page<Transaction> transactionPage = transactionRepository.findAll(pageable);
        List<TransactionResponse> responses = transactionPage.getContent().stream()
                .map(txn -> mappingService.map(txn, TransactionResponse.class))
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, transactionPage.getTotalElements());
    }

    @Override
    public List<TransactionResponse> getSuspiciousTransactions() {
        Date thirtyDaysAgo = getDateDaysAgo(30);
        List<Transaction> suspiciousTransactions = transactionRepository.findSuspiciousWithdrawals(
                SUSPICIOUS_THRESHOLD, thirtyDaysAgo);
        return suspiciousTransactions.stream()
                .map(txn -> mappingService.map(txn, TransactionResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getHighValueTransactions(BigDecimal threshold) {
        List<Transaction> highValueTransactions = transactionRepository.findHighValueTransactions(threshold);
        return highValueTransactions.stream()
                .map(txn -> mappingService.map(txn, TransactionResponse.class))
                .collect(Collectors.toList());
    }

    // Helper methods
    private BigDecimal calculateOpeningBalance(Integer accountId, Date fromDate) {
        // Get all transactions before fromDate
        List<Transaction> previousTransactions = transactionRepository.findByAccountIdAndTxnTimeGreaterThanOrderByTxnTimeAsc(
                accountId, fromDate);
        
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal currentBalance = account.getBalance();
        
        // Subtract all transactions after fromDate to get opening balance
        for (Transaction txn : previousTransactions) {
            if (txn.getTxnType() == Transaction.TxnType.CREDIT) {
                currentBalance = currentBalance.subtract(txn.getAmount());
            } else {
                currentBalance = currentBalance.add(txn.getAmount());
            }
        }
        
        return currentBalance;
    }
    
    private Date getTodayStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    private Date getTodayEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    private Date getTodayStart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    private Date getTodayEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    private Date getDateDaysAgo(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -days);
        return cal.getTime();
    }
}
