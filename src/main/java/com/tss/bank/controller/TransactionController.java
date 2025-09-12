package com.tss.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tss.bank.dto.request.TransactionRequest;
import com.tss.bank.dto.request.TransactionHistoryRequest;
import com.tss.bank.dto.response.TransactionResponse;
import com.tss.bank.dto.response.AccountStatementResponse;
import com.tss.bank.dto.response.ApiResponse;
import com.tss.bank.service.TransactionService;
import com.tss.bank.service.AuthorizationService;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AuthorizationService authorizationService;

    // Core Transaction Operations
    @PostMapping("/deposit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransactionResponse>> processDeposit(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse transactionResponse = transactionService.processDeposit(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Deposit processed successfully", transactionResponse));
    }

    @PostMapping("/withdrawal")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransactionResponse>> processWithdrawal(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse transactionResponse = transactionService.processWithdrawal(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Withdrawal processed successfully", transactionResponse));
    }

    @GetMapping("/{txnId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionDetails(@PathVariable Integer txnId) {
        TransactionResponse transactionResponse = transactionService.getTransactionDetails(txnId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transaction details retrieved successfully", transactionResponse));
    }

    // Transaction History
    @PostMapping("/history")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactionHistory(@Valid @RequestBody TransactionHistoryRequest request) {
        Page<TransactionResponse> transactions = transactionService.getTransactionHistory(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transaction history retrieved successfully", transactions));
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getAccountTransactions(@PathVariable Integer accountId) {
        authorizationService.validateAccountAccess(accountId);
        List<TransactionResponse> transactions = transactionService.getAccountTransactions(accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account transactions retrieved successfully", transactions));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getUserTransactions(@PathVariable Integer userId) {
        authorizationService.validateUserAccess(userId);
        List<TransactionResponse> transactions = transactionService.getUserTransactions(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User transactions retrieved successfully", transactions));
    }

    @GetMapping("/statement/{accountId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AccountStatementResponse>> generateAccountStatement(
            @PathVariable Integer accountId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
        authorizationService.validateAccountAccess(accountId);
        AccountStatementResponse statement = transactionService.generateAccountStatement(accountId, fromDate, toDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account statement generated successfully", statement));
    }

    // Transaction Validation
    @PostMapping("/validate")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validateTransaction(@Valid @RequestBody TransactionRequest request) {
        boolean isValid = transactionService.validateTransaction(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transaction validation completed", isValid));
    }

    @GetMapping("/{accountId}/validate-withdrawal/{amount}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validateWithdrawalLimit(
            @PathVariable Integer accountId,
            @PathVariable BigDecimal amount) {
        boolean isValid = transactionService.validateWithdrawalLimit(accountId, amount);
        return ResponseEntity.ok(new ApiResponse<>(true, "Withdrawal limit validation completed", isValid));
    }

    @GetMapping("/{accountId}/validate-daily-limit/{amount}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validateDailyLimit(
            @PathVariable Integer accountId,
            @PathVariable BigDecimal amount) {
        boolean isValid = transactionService.validateDailyLimit(accountId, amount);
        return ResponseEntity.ok(new ApiResponse<>(true, "Daily limit validation completed", isValid));
    }

    @GetMapping("/{accountId}/transaction-allowed/{amount}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> isTransactionAllowed(
            @PathVariable Integer accountId,
            @PathVariable BigDecimal amount) {
        boolean isAllowed = transactionService.isTransactionAllowed(accountId, amount);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transaction allowance check completed", isAllowed));
    }

    // Transaction Processing
    @PostMapping("/{txnId}/reverse")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> reverseTransaction(
            @PathVariable Integer txnId,
            @RequestParam String reason) {
        transactionService.reverseTransaction(txnId, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transaction reversed successfully", "Transaction has been reversed"));
    }

    // Analytics
    @GetMapping("/{accountId}/analytics/debit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalDebitAmount(
            @PathVariable Integer accountId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
        BigDecimal totalDebit = transactionService.getTotalDebitAmount(accountId, fromDate, toDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Total debit amount retrieved successfully", totalDebit));
    }

    @GetMapping("/{accountId}/analytics/credit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalCreditAmount(
            @PathVariable Integer accountId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
        BigDecimal totalCredit = transactionService.getTotalCreditAmount(accountId, fromDate, toDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Total credit amount retrieved successfully", totalCredit));
    }

    @GetMapping("/{accountId}/analytics/daily/{date}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getDailyTransactionAmount(
            @PathVariable Integer accountId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        BigDecimal dailyAmount = transactionService.getDailyTransactionAmount(accountId, date);
        return ResponseEntity.ok(new ApiResponse<>(true, "Daily transaction amount retrieved successfully", dailyAmount));
    }

    @GetMapping("/{accountId}/analytics/count")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getTransactionCount(
            @PathVariable Integer accountId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
        long count = transactionService.getTransactionCount(accountId, fromDate, toDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transaction count retrieved successfully", count));
    }

    // Limits and Controls
    @GetMapping("/limits/daily-withdrawal")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getDailyWithdrawalLimit() {
        BigDecimal limit = transactionService.getDailyWithdrawalLimit();
        return ResponseEntity.ok(new ApiResponse<>(true, "Daily withdrawal limit retrieved successfully", limit));
    }

    @GetMapping("/limits/per-transaction")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getPerTransactionLimit() {
        BigDecimal limit = transactionService.getPerTransactionLimit();
        return ResponseEntity.ok(new ApiResponse<>(true, "Per transaction limit retrieved successfully", limit));
    }

    // Admin Operations
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getAllTransactions(Pageable pageable) {
        Page<TransactionResponse> transactions = transactionService.getAllTransactions(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "All transactions retrieved successfully", transactions));
    }

    @GetMapping("/suspicious")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getSuspiciousTransactions() {
        List<TransactionResponse> transactions = transactionService.getSuspiciousTransactions();
        return ResponseEntity.ok(new ApiResponse<>(true, "Suspicious transactions retrieved successfully", transactions));
    }

    @GetMapping("/high-value/{threshold}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getHighValueTransactions(@PathVariable BigDecimal threshold) {
        List<TransactionResponse> transactions = transactionService.getHighValueTransactions(threshold);
        return ResponseEntity.ok(new ApiResponse<>(true, "High value transactions retrieved successfully", transactions));
    }
}
