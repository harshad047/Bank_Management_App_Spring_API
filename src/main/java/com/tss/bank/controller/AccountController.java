package com.tss.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tss.bank.dto.request.AccountCreationRequest;
import com.tss.bank.dto.request.BalanceInquiryRequest;
import com.tss.bank.dto.response.AccountResponse;
import com.tss.bank.dto.response.ApiResponse;
import com.tss.bank.dto.response.BalanceInquiryResponse;
import com.tss.bank.service.AccountService;
import com.tss.bank.service.AuthorizationService;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;
    
    @Autowired
    private AuthorizationService authorizationService;

    // Account Management
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(@Valid @RequestBody AccountCreationRequest request) {
        Integer userId = authorizationService.getCurrentUserId();
        AccountResponse accountResponse = accountService.createAccount(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Account created successfully", accountResponse));
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountDetails(@PathVariable Integer accountId) {
        AccountResponse accountResponse = accountService.getAccountDetails(accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account details retrieved successfully", accountResponse));
    }

    @GetMapping("/my-accounts")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getMyAccounts() {
        Integer userId = authorizationService.getCurrentUserId();
        List<AccountResponse> accounts = accountService.getUserAccounts(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User accounts retrieved successfully", accounts));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getUserAccounts(@PathVariable Integer userId) {
        List<AccountResponse> accounts = accountService.getUserAccounts(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User accounts retrieved successfully", accounts));
    }

    @PostMapping("/balance-inquiry")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BalanceInquiryResponse>> checkBalance(@Valid @RequestBody BalanceInquiryRequest request) {
        BalanceInquiryResponse balanceResponse = accountService.checkBalance(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Balance inquiry completed successfully", balanceResponse));
    }

    // Balance Operations
    @GetMapping("/{accountId}/balance/available")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getAvailableBalance(@PathVariable Integer accountId) {
        BigDecimal balance = accountService.getAvailableBalance(accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Available balance retrieved successfully", balance));
    }

    @GetMapping("/{accountId}/balance/total")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalBalance(@PathVariable Integer accountId) {
        BigDecimal balance = accountService.getTotalBalance(accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Total balance retrieved successfully", balance));
    }

    @GetMapping("/my-total-balance")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getMyTotalBalance() {
        Integer userId = authorizationService.getCurrentUserId();
        BigDecimal totalBalance = accountService.getTotalBalanceByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Total user balance retrieved successfully", totalBalance));
    }

    @GetMapping("/user/{userId}/total-balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalBalanceByUser(@PathVariable Integer userId) {
        BigDecimal totalBalance = accountService.getTotalBalanceByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Total user balance retrieved successfully", totalBalance));
    }

    // Account Status Management
    @PostMapping("/{accountId}/freeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> freezeAccount(@PathVariable Integer accountId) {
        accountService.freezeAccount(accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account frozen successfully", "Account has been frozen"));
    }

    @PostMapping("/{accountId}/unfreeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> unfreezeAccount(@PathVariable Integer accountId) {
        accountService.unfreezeAccount(accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account unfrozen successfully", "Account has been unfrozen"));
    }

    @PostMapping("/{accountId}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> closeAccount(@PathVariable Integer accountId) {
        accountService.closeAccount(accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account closed successfully", "Account has been closed"));
    }

    @GetMapping("/{accountId}/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> isAccountActive(@PathVariable Integer accountId) {
        boolean isActive = accountService.isAccountActive(accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account status retrieved successfully", isActive));
    }

    // Validation Operations
    @GetMapping("/{accountId}/ownership/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validateAccountOwnership(
            @PathVariable Integer accountId,
            @PathVariable Integer userId) {
        boolean isOwner = accountService.validateAccountOwnership(accountId, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account ownership validation completed", isOwner));
    }

    @GetMapping("/{accountId}/minimum-balance/{amount}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> hasMinimumBalance(
            @PathVariable Integer accountId,
            @PathVariable BigDecimal amount) {
        boolean hasMinBalance = accountService.hasMinimumBalance(accountId, amount);
        return ResponseEntity.ok(new ApiResponse<>(true, "Minimum balance check completed", hasMinBalance));
    }

    // Admin Operations
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AccountResponse>>> getAllAccounts(Pageable pageable) {
        Page<AccountResponse> accounts = accountService.findAllAccounts(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "All accounts retrieved successfully", accounts));
    }

    @GetMapping("/minimum-balance/{minBalance}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAccountsByMinBalance(@PathVariable BigDecimal minBalance) {
        List<AccountResponse> accounts = accountService.findAccountsByMinBalance(minBalance);
        return ResponseEntity.ok(new ApiResponse<>(true, "Accounts by minimum balance retrieved successfully", accounts));
    }

    // Statistics
    @GetMapping("/stats/total-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getTotalAccountCount() {
        long count = accountService.getTotalAccountCount();
        return ResponseEntity.ok(new ApiResponse<>(true, "Total account count retrieved successfully", count));
    }

    @GetMapping("/stats/active-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getActiveAccountCount() {
        long count = accountService.getActiveAccountCount();
        return ResponseEntity.ok(new ApiResponse<>(true, "Active account count retrieved successfully", count));
    }

    // Utility
    @GetMapping("/generate-account-number")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> generateAccountNumber() {
        String accountNumber = accountService.generateAccountNumber();
        return ResponseEntity.ok(new ApiResponse<>(true, "Account number generated successfully", accountNumber));
    }
}
