package com.tss.bank.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tss.bank.dto.request.TransactionRequest;
import com.tss.bank.dto.request.TransactionHistoryRequest;
import com.tss.bank.dto.response.TransactionResponse;
import com.tss.bank.dto.response.AccountStatementResponse;
import com.tss.bank.entity.Transaction;

public interface TransactionService {
    
    // Core Transaction Operations
    TransactionResponse processDeposit(TransactionRequest request);
    TransactionResponse processWithdrawal(TransactionRequest request);
    TransactionResponse getTransactionDetails(Integer txnId);
    
    // Transaction History
    Page<TransactionResponse> getTransactionHistory(TransactionHistoryRequest request);
    List<TransactionResponse> getAccountTransactions(Integer accountId);
    List<TransactionResponse> getUserTransactions(Integer userId);
    AccountStatementResponse generateAccountStatement(Integer accountId, Date fromDate, Date toDate);
    
    // Transaction Validation
    boolean validateTransaction(TransactionRequest request);
    boolean validateWithdrawalLimit(Integer accountId, BigDecimal amount);
    boolean validateDailyLimit(Integer accountId, BigDecimal amount);
    
    // Transaction Processing
    void recordTransaction(Integer accountId, Transaction.TxnType type, BigDecimal amount, 
                          String description, Transaction.Channel channel);
    void reverseTransaction(Integer txnId, String reason);
    
    // Analytics
    BigDecimal getTotalDebitAmount(Integer accountId, Date fromDate, Date toDate);
    BigDecimal getTotalCreditAmount(Integer accountId, Date fromDate, Date toDate);
    BigDecimal getDailyTransactionAmount(Integer accountId, Date date);
    long getTransactionCount(Integer accountId, Date fromDate, Date toDate);
    
    // Limits and Controls
    BigDecimal getDailyWithdrawalLimit();
    BigDecimal getPerTransactionLimit();
    boolean isTransactionAllowed(Integer accountId, BigDecimal amount);
    
    // Admin Operations
    Page<TransactionResponse> getAllTransactions(Pageable pageable);
    List<TransactionResponse> getSuspiciousTransactions();
    List<TransactionResponse> getHighValueTransactions(BigDecimal threshold);
}
