package com.tss.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tss.bank.dto.request.BeneficiaryRequest;
import com.tss.bank.dto.response.BeneficiaryResponse;
import com.tss.bank.dto.response.ApiResponse;
import com.tss.bank.service.BeneficiaryService;
import com.tss.bank.service.AuthorizationService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/beneficiaries")
@CrossOrigin(origins = "*")
public class BeneficiaryController {

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private AuthorizationService authorizationService;

    // Main Operations
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> addBeneficiary(@Valid @RequestBody BeneficiaryRequest request) {
        BeneficiaryResponse beneficiaryResponse = beneficiaryService.addBeneficiary(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Beneficiary added successfully", beneficiaryResponse));
    }

    @PutMapping("/{beneficiaryId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> updateBeneficiary(
            @PathVariable Integer beneficiaryId,
            @Valid @RequestBody BeneficiaryRequest request) {
        BeneficiaryResponse beneficiaryResponse = beneficiaryService.updateBeneficiary(beneficiaryId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Beneficiary updated successfully", beneficiaryResponse));
    }

    @DeleteMapping("/{beneficiaryId}/account/{accountId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteBeneficiary(
            @PathVariable Integer beneficiaryId,
            @PathVariable Integer accountId) {
        authorizationService.validateAccountAccess(accountId);
        beneficiaryService.deleteBeneficiary(beneficiaryId, accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Beneficiary deleted successfully", "Beneficiary removed"));
    }

    @GetMapping("/{beneficiaryId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> getBeneficiaryDetails(@PathVariable Integer beneficiaryId) {
        BeneficiaryResponse beneficiaryResponse = beneficiaryService.getBeneficiaryDetails(beneficiaryId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Beneficiary details retrieved successfully", beneficiaryResponse));
    }

    // Query Operations
    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BeneficiaryResponse>>> getAccountBeneficiaries(@PathVariable Integer accountId) {
        authorizationService.validateAccountAccess(accountId);
        List<BeneficiaryResponse> beneficiaries = beneficiaryService.getAccountBeneficiaries(accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account beneficiaries retrieved successfully", beneficiaries));
    }

    @GetMapping("/account/{accountId}/validate/{beneficiaryAccountNumber}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validateBeneficiary(
            @PathVariable Integer accountId,
            @PathVariable String beneficiaryAccountNumber) {
        authorizationService.validateAccountAccess(accountId);
        boolean isValid = beneficiaryService.validateBeneficiary(accountId, beneficiaryAccountNumber);
        return ResponseEntity.ok(new ApiResponse<>(true, "Beneficiary validation completed", isValid));
    }

    @GetMapping("/account/{accountId}/find/{beneficiaryAccountNumber}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> findBeneficiaryByAccountNumber(
            @PathVariable Integer accountId,
            @PathVariable String beneficiaryAccountNumber) {
        Optional<BeneficiaryResponse> beneficiary = beneficiaryService.findBeneficiaryByAccountNumber(accountId, beneficiaryAccountNumber);
        if (beneficiary.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Beneficiary found", beneficiary.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/account/{accountId}/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BeneficiaryResponse>>> searchBeneficiaries(
            @PathVariable Integer accountId,
            @RequestParam String searchTerm) {
        List<BeneficiaryResponse> beneficiaries = beneficiaryService.searchBeneficiaries(accountId, searchTerm);
        return ResponseEntity.ok(new ApiResponse<>(true, "Beneficiary search completed", beneficiaries));
    }

    // Statistics
    @GetMapping("/account/{accountId}/count")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getBeneficiaryCount(@PathVariable Integer accountId) {
        authorizationService.validateAccountAccess(accountId);
        long count = beneficiaryService.getBeneficiaryCount(accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Beneficiary count retrieved successfully", count));
    }

    @GetMapping("/account/{accountId}/max-reached")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> isMaxBeneficiariesReached(@PathVariable Integer accountId) {
        boolean maxReached = beneficiaryService.isMaxBeneficiariesReached(accountId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Max beneficiaries check completed", maxReached));
    }

    @GetMapping("/stats/total-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getTotalBeneficiaryCount() {
        long count = beneficiaryService.getTotalBeneficiaryCount();
        return ResponseEntity.ok(new ApiResponse<>(true, "Total beneficiary count retrieved successfully", count));
    }

    // Admin Operations
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<BeneficiaryResponse>>> getAllBeneficiaries(Pageable pageable) {
        Page<BeneficiaryResponse> beneficiaries = beneficiaryService.findAllBeneficiaries(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "All beneficiaries retrieved successfully", beneficiaries));
    }
}
