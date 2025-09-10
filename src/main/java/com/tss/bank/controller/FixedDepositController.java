package com.tss.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tss.bank.dto.request.FixedDepositRequest;
import com.tss.bank.dto.response.FixedDepositResponse;
import com.tss.bank.dto.response.ApiResponse;
import com.tss.bank.service.FixedDepositService;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fixed-deposits")
@CrossOrigin(origins = "*")
public class FixedDepositController {

    @Autowired
    private FixedDepositService fixedDepositService;

    // Main Operations
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FixedDepositResponse>> createFixedDeposit(@Valid @RequestBody FixedDepositRequest request) {
        FixedDepositResponse fdResponse = fixedDepositService.createFixedDeposit(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Fixed deposit created successfully", fdResponse));
    }

    @GetMapping("/{fdId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FixedDepositResponse>> getFixedDepositDetails(@PathVariable Integer fdId) {
        FixedDepositResponse fdResponse = fixedDepositService.getFixedDepositDetails(fdId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fixed deposit details retrieved successfully", fdResponse));
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<FixedDepositResponse>>> getAccountFixedDeposits(@PathVariable Integer accountId) {
        List<FixedDepositResponse> fixedDeposits = fixedDepositService.getAccountFixedDeposits(accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account fixed deposits retrieved successfully", fixedDeposits));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<FixedDepositResponse>>> getUserFixedDeposits(@PathVariable Integer userId) {
        List<FixedDepositResponse> fixedDeposits = fixedDepositService.getUserFixedDeposits(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User fixed deposits retrieved successfully", fixedDeposits));
    }

    @PostMapping("/{fdId}/premature-withdrawal")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FixedDepositResponse>> prematureWithdrawal(
            @PathVariable Integer fdId,
            @RequestParam String reason) {
        FixedDepositResponse fdResponse = fixedDepositService.prematureWithdrawal(fdId, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "Premature withdrawal processed successfully", fdResponse));
    }

    @PostMapping("/{fdId}/mature")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FixedDepositResponse>> matureFixedDeposit(@PathVariable Integer fdId) {
        FixedDepositResponse fdResponse = fixedDepositService.matureFixedDeposit(fdId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fixed deposit matured successfully", fdResponse));
    }

    // Business Logic Calculations
    @GetMapping("/calculate/interest-rate/{tenureMonths}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> calculateInterestRate(@PathVariable Integer tenureMonths) {
        BigDecimal interestRate = fixedDepositService.calculateInterestRate(tenureMonths);
        return ResponseEntity.ok(new ApiResponse<>(true, "Interest rate calculated successfully", interestRate));
    }

    @GetMapping("/calculate/maturity-amount")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> calculateMaturityAmount(
            @RequestParam BigDecimal principal,
            @RequestParam BigDecimal interestRate,
            @RequestParam Integer tenureMonths) {
        BigDecimal maturityAmount = fixedDepositService.calculateMaturityAmount(principal, interestRate, tenureMonths);
        return ResponseEntity.ok(new ApiResponse<>(true, "Maturity amount calculated successfully", maturityAmount));
    }

    @GetMapping("/calculate/maturity-date")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Date>> calculateMaturityDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam Integer tenureMonths) {
        Date maturityDate = fixedDepositService.calculateMaturityDate(startDate, tenureMonths);
        return ResponseEntity.ok(new ApiResponse<>(true, "Maturity date calculated successfully", maturityDate));
    }

    // Maturity Processing
    @GetMapping("/matured")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<FixedDepositResponse>>> getMaturedDeposits() {
        List<FixedDepositResponse> maturedDeposits = fixedDepositService.getMaturedDeposits();
        return ResponseEntity.ok(new ApiResponse<>(true, "Matured deposits retrieved successfully", maturedDeposits));
    }

    @PostMapping("/process-matured")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> processMaturedDeposits() {
        fixedDepositService.processMaturedDeposits();
        return ResponseEntity.ok(new ApiResponse<>(true, "Matured deposits processed successfully", "All matured deposits have been processed"));
    }

    // Analytics
    @GetMapping("/account/{accountId}/total-active")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalActiveDeposits(@PathVariable Integer accountId) {
        BigDecimal totalActive = fixedDepositService.getTotalActiveDeposits(accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Total active deposits retrieved successfully", totalActive));
    }

    @GetMapping("/stats/total-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getTotalFixedDepositCount() {
        long count = fixedDepositService.getTotalFixedDepositCount();
        return ResponseEntity.ok(new ApiResponse<>(true, "Total fixed deposit count retrieved successfully", count));
    }

    @GetMapping("/stats/active-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getActiveFixedDepositCount() {
        long count = fixedDepositService.getActiveFixedDepositCount();
        return ResponseEntity.ok(new ApiResponse<>(true, "Active fixed deposit count retrieved successfully", count));
    }

    @GetMapping("/stats/matured-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getMaturedFixedDepositCount() {
        long count = fixedDepositService.getMaturedFixedDepositCount();
        return ResponseEntity.ok(new ApiResponse<>(true, "Matured fixed deposit count retrieved successfully", count));
    }

    // Admin Operations
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<FixedDepositResponse>>> getAllFixedDeposits(Pageable pageable) {
        Page<FixedDepositResponse> fixedDeposits = fixedDepositService.findAllFixedDeposits(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "All fixed deposits retrieved successfully", fixedDeposits));
    }
}
