package com.tss.bank.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.bank.dto.request.AccountCreationRequest;
import com.tss.bank.dto.request.BalanceInquiryRequest;
import com.tss.bank.dto.response.AccountResponse;
import com.tss.bank.dto.response.BalanceInquiryResponse;
import com.tss.bank.entity.Account;
import com.tss.bank.entity.Branch;
import com.tss.bank.entity.Transaction;
import com.tss.bank.entity.User;
import com.tss.bank.exception.AccountApiException;
import com.tss.bank.repository.AccountRepository;
import com.tss.bank.repository.BranchRepository;
import com.tss.bank.repository.TransactionRepository;
import com.tss.bank.repository.UserRepository;
import com.tss.bank.service.AccountService;
import com.tss.bank.service.MappingService;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private BranchRepository branchRepository;
    
    @Autowired
    private MappingService mappingService;
    
    private static final BigDecimal MINIMUM_BALANCE = new BigDecimal("500.00");

    @Override
    public AccountResponse createAccount(AccountCreationRequest request) {
        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AccountApiException("User not found with ID: " + request.getUserId()));
        
        // Check if user is active
        if (user.getStatus() != User.Status.ACTIVE) {
            throw new AccountApiException("User account is not active");
        }
        
        // Validate minimum balance
        if (request.getInitialBalance().compareTo(MINIMUM_BALANCE) < 0) {
            throw new AccountApiException("Initial balance must be at least " + MINIMUM_BALANCE);
        }
        
        // Generate unique account number
        String accountNumber = generateAccountNumber();
        while (existsByAccountNumber(accountNumber)) {
            accountNumber = generateAccountNumber();
        }
        
        // Validate and get branch
        Branch branch = validateAndGetBranch(request.getBranchCode());
        
        // Create account
        Account account = Account.builder()
                .user(user)
                .branch(branch)
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .balance(request.getInitialBalance())
                .status(Account.Status.ACTIVE)
                .createdAt(new Date())
                .build();
        
        Account savedAccount = accountRepository.save(account);
        
        // Record initial deposit transaction
        recordInitialDeposit(savedAccount, request.getInitialBalance());
        
        return mappingService.map(savedAccount, AccountResponse.class);
    }

    @Override
    public AccountResponse getAccountDetails(Integer accountId) {
        Account account = findById(accountId)
                .orElseThrow(() -> new AccountApiException("Account not found with ID: " + accountId));
        return mappingService.map(account, AccountResponse.class);
    }

    @Override
    public List<AccountResponse> getUserAccounts(Integer userId) {
        List<Account> accounts = findByUserId(userId);
        return mappingService.mapList(accounts, AccountResponse.class);
    }

    @Override
    public BalanceInquiryResponse checkBalance(BalanceInquiryRequest request) {
        Account account = findById(request.getAccountId())
                .orElseThrow(() -> new AccountApiException("Account not found with ID: " + request.getAccountId()));
        
        // Get last transaction date
        Date lastTransactionDate = transactionRepository
                .findTopByAccountAccountIdOrderByTxnTimeDesc(account.getAccountId())
                .map(Transaction::getTxnTime)
                .orElse(account.getCreatedAt());
        
        return BalanceInquiryResponse.builder()
                .accountId(account.getAccountId())
                .accountNumber(account.getAccountNumber())
                .availableBalance(account.getBalance())
                .totalBalance(account.getBalance())
                .lastTransactionDate(lastTransactionDate)
                .inquiryTime(new Date())
                .build();
    }

    @Override
    public boolean validateAccountOwnership(Integer accountId, Integer userId) {
        return accountRepository.existsByAccountIdAndUserUserId(accountId, userId);
    }

    @Override
    public boolean hasMinimumBalance(Integer accountId, BigDecimal amount) {
        Account account = findById(accountId)
                .orElseThrow(() -> new AccountApiException("Account not found"));
        
        BigDecimal balanceAfterDebit = account.getBalance().subtract(amount);
        return balanceAfterDebit.compareTo(MINIMUM_BALANCE) >= 0;
    }

    @Override
    public String generateAccountNumber() {
        // Generate account number with format: AC + 6 digit unique number
        String prefix = "AC";
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder(prefix);
        
        for (int i = 0; i < 6; i++) {
            accountNumber.append(random.nextInt(10));
        }
        
        return accountNumber.toString();
    }

    @Override
    @Transactional
    public void creditAmount(Integer accountId, BigDecimal amount, String description) {
        Account account = findById(accountId)
                .orElseThrow(() -> new AccountApiException("Account not found"));
        
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);
        
        // Record transaction
        recordTransaction(account, Transaction.TxnType.CREDIT, amount, description, newBalance);
    }

    @Override
    @Transactional
    public void debitAmount(Integer accountId, BigDecimal amount, String description) {
        Account account = findById(accountId)
                .orElseThrow(() -> new AccountApiException("Account not found"));
        
        // Check sufficient balance
        if (!hasMinimumBalance(accountId, amount)) {
            throw new AccountApiException("Insufficient balance. Minimum balance of " + MINIMUM_BALANCE + " required");
        }
        
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);
        
        // Record transaction
        recordTransaction(account, Transaction.TxnType.DEBIT, amount, description, newBalance);
    }

    @Override
    public BigDecimal getAvailableBalance(Integer accountId) {
        Account account = findById(accountId)
                .orElseThrow(() -> new AccountApiException("Account not found"));
        return account.getBalance();
    }

    @Override
    public BigDecimal getTotalBalance(Integer accountId) {
        return getAvailableBalance(accountId);
    }

    @Override
    public void freezeAccount(Integer accountId) {
        // Implementation would involve adding account status field
        // For now, we'll throw an exception to indicate frozen account
        throw new AccountApiException("Account operations are frozen");
    }

    @Override
    public void unfreezeAccount(Integer accountId) {
        // Implementation would involve updating account status
    }

    @Override
    public void closeAccount(Integer accountId) {
        Account account = findById(accountId)
                .orElseThrow(() -> new AccountApiException("Account not found"));
        
        // Check if balance is zero
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new AccountApiException("Cannot close account with non-zero balance");
        }
        
        // In real implementation, we would set status to CLOSED instead of deleting
        accountRepository.delete(account);
    }

    @Override
    public boolean isAccountActive(Integer accountId) {
        return findById(accountId).isPresent();
    }

    @Override
    public Optional<Account> findById(Integer accountId) {
        return accountRepository.findById(accountId);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public List<Account> findByUserId(Integer userId) {
        return accountRepository.findByUserUserId(userId);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }

    @Override
    public BigDecimal getTotalBalanceByUserId(Integer userId) {
        return accountRepository.getTotalBalanceByUserId(userId);
    }

    @Override
    public long getTotalAccountCount() {
        return accountRepository.count();
    }

    @Override
    public long getActiveAccountCount() {
        return accountRepository.count(); // All accounts are considered active for now
    }

    @Override
    public Page<AccountResponse> findAllAccounts(Pageable pageable) {
        Page<Account> accounts = accountRepository.findAll(pageable);
        return accounts.map(account -> mappingService.map(account, AccountResponse.class));
    }

    @Override
    public List<AccountResponse> findAccountsByMinBalance(BigDecimal minBalance) {
        List<Account> accounts = accountRepository.findByBalanceGreaterThanEqual(minBalance);
        return mappingService.mapList(accounts, AccountResponse.class);
    }
    
    private void recordInitialDeposit(Account account, BigDecimal amount) {
        Transaction transaction = Transaction.builder()
                .user(account.getUser())
                .account(account)
                .txnType(Transaction.TxnType.CREDIT)
                .amount(amount)
                .description("Initial deposit")
                .txnTime(new Date())
                .balanceAfter(amount)
                .channel(Transaction.Channel.BRANCH)
                .createdAt(new Date())
                .build();
        
        transactionRepository.save(transaction);
    }
    
    private void recordTransaction(Account account, Transaction.TxnType type, BigDecimal amount, 
                                 String description, BigDecimal balanceAfter) {
        Transaction transaction = Transaction.builder()
                .user(account.getUser())
                .account(account)
                .txnType(type)
                .amount(amount)
                .description(description)
                .txnTime(new Date())
                .balanceAfter(balanceAfter)
                .channel(Transaction.Channel.ONLINE)
                .createdAt(new Date())
                .build();
        
        transactionRepository.save(transaction);
    }
    
    /**
     * Validates branch code exists and is active
     */
    private Branch validateAndGetBranch(String branchCode) {
        if (branchCode == null || branchCode.trim().isEmpty()) {
            throw new AccountApiException("Branch code is required for account creation");
        }
        
        Branch branch = branchRepository.findByBranchCode(branchCode)
                .orElseThrow(() -> new AccountApiException("Branch not found with code: " + branchCode));
        
        if (branch.getStatus() != Branch.Status.ACTIVE) {
            throw new AccountApiException("Branch with code " + branchCode + " is not active");
        }
        
        return branch;
    }
}
