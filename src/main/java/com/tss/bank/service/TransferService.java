package com.tss.bank.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tss.bank.dto.request.TransferRequest;
import com.tss.bank.dto.request.TransferConfirmationRequest;
import com.tss.bank.dto.response.TransferResponse;
import com.tss.bank.dto.response.TransferConfirmationResponse;

public interface TransferService {
    
    // Fund Transfer Operations
    TransferResponse initiateTransfer(TransferRequest request);
    TransferConfirmationResponse confirmTransfer(TransferConfirmationRequest request);
    TransferResponse getTransferDetails(Integer transferId);
    
    // Transfer Validation
    boolean validateTransfer(TransferRequest request);
    boolean validateBeneficiaryAccount(String accountNumber, String ifscCode);
    boolean validateTransferLimits(Integer fromAccountId, BigDecimal amount);
    boolean validateDailyTransferLimit(Integer fromAccountId, BigDecimal amount);
    
    // Transfer Processing
    String generateTransferReference();
    void processTransfer(Integer fromAccountId, Integer toAccountId, BigDecimal amount, String description);
    void reverseTransfer(Integer transferId, String reason);
    
    // OTP and Security
    String generateTransferOTP(Integer transferId);
    boolean verifyTransferOTP(Integer transferId, String otp);
    boolean validateTransactionPassword(Integer userId, String transactionPassword);
    
    // Transfer History
    List<TransferResponse> getTransferHistory(Integer accountId);
    Page<TransferResponse> getTransferHistoryPaginated(Integer accountId, Pageable pageable);
    List<TransferResponse> getTransfersByDateRange(Integer accountId, Date fromDate, Date toDate);
    
    // Analytics
    BigDecimal getTotalTransferredAmount(Integer accountId, Date fromDate, Date toDate);
    BigDecimal getDailyTransferAmount(Integer accountId, Date date);
    long getTransferCount(Integer accountId, Date fromDate, Date toDate);
    
    // Limits and Controls
    BigDecimal getDailyTransferLimit();
    BigDecimal getPerTransferLimit();
    BigDecimal getMonthlyTransferLimit();
    
    // Admin Operations
    Page<TransferResponse> getAllTransfers(Pageable pageable);
    List<TransferResponse> getHighValueTransfers(BigDecimal threshold);
    List<TransferResponse> getPendingTransfers();
    List<TransferResponse> getFailedTransfers();
    
    // Additional methods needed by controller
    String generateOTP(Integer transferId);
    boolean verifyOTP(Integer transferId, String otp);
    TransferResponse processTransfer(Integer transferId);
    BigDecimal getMonthlyTransferAmount(Integer accountId, Date date);
    boolean isTransferAllowed(Integer fromAccountId, BigDecimal amount);
}
