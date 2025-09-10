package com.tss.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tss.bank.dto.request.TransferRequest;
import com.tss.bank.dto.request.TransferConfirmationRequest;
import com.tss.bank.dto.response.TransferResponse;
import com.tss.bank.dto.response.TransferConfirmationResponse;
import com.tss.bank.dto.response.ApiResponse;
import com.tss.bank.service.TransferService;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transfers")
@CrossOrigin(origins = "*")
public class TransferController {

    @Autowired
    private TransferService transferService;

    // Fund Transfer Operations
    @PostMapping("/initiate")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransferResponse>> initiateTransfer(@Valid @RequestBody TransferRequest request) {
        TransferResponse transferResponse = transferService.initiateTransfer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Transfer initiated successfully", transferResponse));
    }

    @PostMapping("/confirm")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransferConfirmationResponse>> confirmTransfer(@Valid @RequestBody TransferConfirmationRequest request) {
        TransferConfirmationResponse confirmationResponse = transferService.confirmTransfer(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transfer confirmed successfully", confirmationResponse));
    }

    @GetMapping("/details/{transferId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransferResponse>> getTransferDetails(@PathVariable Integer transferId) {
        TransferResponse transferResponse = transferService.getTransferDetails(transferId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transfer details retrieved successfully", transferResponse));
    }

    // Transfer Validation
    @PostMapping("/validate")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validateTransfer(@Valid @RequestBody TransferRequest request) {
        boolean isValid = transferService.validateTransfer(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transfer validation completed", isValid));
    }

    @GetMapping("/validate-beneficiary")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validateBeneficiaryAccount(
            @RequestParam String accountNumber,
            @RequestParam String ifscCode) {
        boolean isValid = transferService.validateBeneficiaryAccount(accountNumber, ifscCode);
        return ResponseEntity.ok(new ApiResponse<>(true, "Beneficiary account validation completed", isValid));
    }

    @GetMapping("/{fromAccountId}/validate-limits/{amount}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validateTransferLimits(
            @PathVariable Integer fromAccountId,
            @PathVariable BigDecimal amount) {
        boolean isValid = transferService.validateTransferLimits(fromAccountId, amount);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transfer limits validation completed", isValid));
    }

    @GetMapping("/{fromAccountId}/validate-daily-limit/{amount}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validateDailyTransferLimit(
            @PathVariable Integer fromAccountId,
            @PathVariable BigDecimal amount) {
        boolean isValid = transferService.validateDailyTransferLimit(fromAccountId, amount);
        return ResponseEntity.ok(new ApiResponse<>(true, "Daily transfer limit validation completed", isValid));
    }

    @GetMapping("/{fromAccountId}/transfer-allowed/{amount}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> isTransferAllowed(
            @PathVariable Integer fromAccountId,
            @PathVariable BigDecimal amount) {
        boolean isAllowed = transferService.isTransferAllowed(fromAccountId, amount);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transfer allowance check completed", isAllowed));
    }

    // OTP and Security
    @PostMapping("/{transferId}/generate-otp")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> generateOTP(@PathVariable Integer transferId) {
        String otp = transferService.generateOTP(transferId);
        return ResponseEntity.ok(new ApiResponse<>(true, "OTP generated successfully", otp));
    }

    @PostMapping("/{transferId}/verify-otp")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> verifyOTP(
            @PathVariable Integer transferId,
            @RequestParam String otp) {
        boolean isValid = transferService.verifyOTP(transferId, otp);
        return ResponseEntity.ok(new ApiResponse<>(true, "OTP verification completed", isValid));
    }

    @PostMapping("/{userId}/validate-transaction-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validateTransactionPassword(
            @PathVariable Integer userId,
            @RequestParam String transactionPassword) {
        boolean isValid = transferService.validateTransactionPassword(userId, transactionPassword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transaction password validation completed", isValid));
    }

    // Transfer Processing
    @PostMapping("/{transferId}/process")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransferResponse>> processTransfer(@PathVariable Integer transferId) {
        TransferResponse transferResponse = transferService.processTransfer(transferId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transfer processed successfully", transferResponse));
    }

    @PostMapping("/{transferId}/reverse")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> reverseTransfer(
            @PathVariable Integer transferId,
            @RequestParam String reason) {
        transferService.reverseTransfer(transferId, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transfer reversed successfully", "Transfer has been reversed"));
    }

    // Transfer History
    @GetMapping("/account/{accountId}/history")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> getTransferHistory(@PathVariable Integer accountId) {
        List<TransferResponse> transfers = transferService.getTransferHistory(accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transfer history retrieved successfully", transfers));
    }

    @GetMapping("/account/{accountId}/history-paginated")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<TransferResponse>>> getTransferHistoryPaginated(
            @PathVariable Integer accountId,
            Pageable pageable) {
        Page<TransferResponse> transfers = transferService.getTransferHistoryPaginated(accountId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Paginated transfer history retrieved successfully", transfers));
    }

    @GetMapping("/account/{accountId}/date-range")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> getTransfersByDateRange(
            @PathVariable Integer accountId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
        List<TransferResponse> transfers = transferService.getTransfersByDateRange(accountId, fromDate, toDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transfers by date range retrieved successfully", transfers));
    }

    // Analytics
    @GetMapping("/{accountId}/analytics/total-amount")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalTransferredAmount(
            @PathVariable Integer accountId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
        BigDecimal totalAmount = transferService.getTotalTransferredAmount(accountId, fromDate, toDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Total transferred amount retrieved successfully", totalAmount));
    }

    @GetMapping("/{accountId}/analytics/daily/{date}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getDailyTransferAmount(
            @PathVariable Integer accountId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        BigDecimal dailyAmount = transferService.getDailyTransferAmount(accountId, date);
        return ResponseEntity.ok(new ApiResponse<>(true, "Daily transfer amount retrieved successfully", dailyAmount));
    }

    @GetMapping("/{accountId}/analytics/monthly/{date}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getMonthlyTransferAmount(
            @PathVariable Integer accountId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        BigDecimal monthlyAmount = transferService.getMonthlyTransferAmount(accountId, date);
        return ResponseEntity.ok(new ApiResponse<>(true, "Monthly transfer amount retrieved successfully", monthlyAmount));
    }

    @GetMapping("/{accountId}/analytics/count")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getTransferCount(
            @PathVariable Integer accountId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
        long count = transferService.getTransferCount(accountId, fromDate, toDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transfer count retrieved successfully", count));
    }

    // Limits and Controls
    @GetMapping("/limits/daily")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getDailyTransferLimit() {
        BigDecimal limit = transferService.getDailyTransferLimit();
        return ResponseEntity.ok(new ApiResponse<>(true, "Daily transfer limit retrieved successfully", limit));
    }

    @GetMapping("/limits/per-transfer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getPerTransferLimit() {
        BigDecimal limit = transferService.getPerTransferLimit();
        return ResponseEntity.ok(new ApiResponse<>(true, "Per transfer limit retrieved successfully", limit));
    }

    @GetMapping("/limits/monthly")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getMonthlyTransferLimit() {
        BigDecimal limit = transferService.getMonthlyTransferLimit();
        return ResponseEntity.ok(new ApiResponse<>(true, "Monthly transfer limit retrieved successfully", limit));
    }

    // Admin Operations
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<TransferResponse>>> getAllTransfers(Pageable pageable) {
        Page<TransferResponse> transfers = transferService.getAllTransfers(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "All transfers retrieved successfully", transfers));
    }

    @GetMapping("/high-value/{threshold}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> getHighValueTransfers(@PathVariable BigDecimal threshold) {
        List<TransferResponse> transfers = transferService.getHighValueTransfers(threshold);
        return ResponseEntity.ok(new ApiResponse<>(true, "High value transfers retrieved successfully", transfers));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> getPendingTransfers() {
        List<TransferResponse> transfers = transferService.getPendingTransfers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Pending transfers retrieved successfully", transfers));
    }

    @GetMapping("/failed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> getFailedTransfers() {
        List<TransferResponse> transfers = transferService.getFailedTransfers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Failed transfers retrieved successfully", transfers));
    }

    // Utility
    @GetMapping("/generate-reference")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> generateTransferReference() {
        String reference = transferService.generateTransferReference();
        return ResponseEntity.ok(new ApiResponse<>(true, "Transfer reference generated successfully", reference));
    }
}
