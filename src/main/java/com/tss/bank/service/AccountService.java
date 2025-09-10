package com.tss.bank.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tss.bank.dto.request.AccountCreationRequest;
import com.tss.bank.dto.request.BalanceInquiryRequest;
import com.tss.bank.dto.response.AccountResponse;
import com.tss.bank.dto.response.BalanceInquiryResponse;
import com.tss.bank.entity.Account;

public interface AccountService {
    
    // Account Management
    AccountResponse createAccount(AccountCreationRequest request);
    AccountResponse getAccountDetails(Integer accountId);
    List<AccountResponse> getUserAccounts(Integer userId);
    BalanceInquiryResponse checkBalance(BalanceInquiryRequest request);
    
    // Account Operations
    boolean validateAccountOwnership(Integer accountId, Integer userId);
    boolean hasMinimumBalance(Integer accountId, BigDecimal amount);
    String generateAccountNumber();
    
    // Balance Operations
    void creditAmount(Integer accountId, BigDecimal amount, String description);
    void debitAmount(Integer accountId, BigDecimal amount, String description);
    BigDecimal getAvailableBalance(Integer accountId);
    BigDecimal getTotalBalance(Integer accountId);
    
    // Account Status
    void freezeAccount(Integer accountId);
    void unfreezeAccount(Integer accountId);
    void closeAccount(Integer accountId);
    boolean isAccountActive(Integer accountId);
    
    // Query Methods
    Optional<Account> findById(Integer accountId);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByUserId(Integer userId);
    boolean existsByAccountNumber(String accountNumber);
    
    // Statistics
    BigDecimal getTotalBalanceByUserId(Integer userId);
    long getTotalAccountCount();
    long getActiveAccountCount();
    
    // Admin Operations
    Page<AccountResponse> findAllAccounts(Pageable pageable);
    List<AccountResponse> findAccountsByMinBalance(BigDecimal minBalance);
}
