package com.tss.bank.controller;

import com.tss.bank.dto.request.BranchRequest;
import com.tss.bank.dto.response.ApiResponse;
import com.tss.bank.dto.response.BranchResponse;
import com.tss.bank.entity.Branch;
import com.tss.bank.service.BranchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
@CrossOrigin(origins = "*")
public class BranchController {

    @Autowired
    private BranchService branchService;

    // CRUD Operations - Admin Only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> createBranch(@Valid @RequestBody BranchRequest request) {
        BranchResponse branchResponse = branchService.createBranch(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Branch created successfully", branchResponse));
    }

    @PutMapping("/{branchId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> updateBranch(
            @PathVariable Integer branchId,
            @Valid @RequestBody BranchRequest request) {
        BranchResponse branchResponse = branchService.updateBranch(branchId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branch updated successfully", branchResponse));
    }

    @DeleteMapping("/{branchId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteBranch(@PathVariable Integer branchId) {
        branchService.deleteBranch(branchId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branch deleted successfully", "Branch has been deleted"));
    }

    // Read Operations - Available to all authenticated users
    @GetMapping("/{branchId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> getBranchDetails(@PathVariable Integer branchId) {
        BranchResponse branchResponse = branchService.getBranchDetails(branchId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branch details retrieved successfully", branchResponse));
    }

    @GetMapping("/code/{branchCode}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> getBranchByCode(@PathVariable String branchCode) {
        BranchResponse branchResponse = branchService.getBranchByCode(branchCode);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branch retrieved successfully", branchResponse));
    }

    @GetMapping("/ifsc/{ifscCode}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> getBranchByIfsc(@PathVariable String ifscCode) {
        BranchResponse branchResponse = branchService.getBranchByIfsc(ifscCode);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branch retrieved successfully", branchResponse));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<BranchResponse>>> getAllBranches(Pageable pageable) {
        Page<BranchResponse> branches = branchService.getAllBranches(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branches retrieved successfully", branches));
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getAllBranchesList() {
        List<BranchResponse> branches = branchService.getAllBranches();
        return ResponseEntity.ok(new ApiResponse<>(true, "Branches list retrieved successfully", branches));
    }

    // Search and Filter Operations
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getBranchesByStatus(@PathVariable Branch.Status status) {
        List<BranchResponse> branches = branchService.getBranchesByStatus(status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branches by status retrieved successfully", branches));
    }

    @GetMapping("/city/{city}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getBranchesByCity(@PathVariable String city) {
        List<BranchResponse> branches = branchService.getBranchesByCity(city);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branches by city retrieved successfully", branches));
    }

    @GetMapping("/state/{state}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getBranchesByState(@PathVariable String state) {
        List<BranchResponse> branches = branchService.getBranchesByState(state);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branches by state retrieved successfully", branches));
    }

    @GetMapping("/location")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getBranchesByCityAndState(
            @RequestParam String city,
            @RequestParam String state) {
        List<BranchResponse> branches = branchService.getBranchesByCityAndState(city, state);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branches by location retrieved successfully", branches));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> searchBranchesByName(@RequestParam String name) {
        List<BranchResponse> branches = branchService.searchBranchesByName(name);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branch search completed successfully", branches));
    }

    // Status Management - Admin Only
    @PostMapping("/{branchId}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> activateBranch(@PathVariable Integer branchId) {
        BranchResponse branchResponse = branchService.activateBranch(branchId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branch activated successfully", branchResponse));
    }

    @PostMapping("/{branchId}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> deactivateBranch(@PathVariable Integer branchId) {
        BranchResponse branchResponse = branchService.deactivateBranch(branchId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branch deactivated successfully", branchResponse));
    }

    @PostMapping("/{branchId}/close")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> closeBranch(@PathVariable Integer branchId) {
        BranchResponse branchResponse = branchService.closeBranch(branchId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branch closed successfully", branchResponse));
    }

    // Validation Operations
    @GetMapping("/validate/code/{branchCode}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validateBranchCode(@PathVariable String branchCode) {
        boolean exists = branchService.existsByBranchCode(branchCode);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branch code validation completed", exists));
    }

    @GetMapping("/validate/ifsc/{ifscCode}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validateIfscCode(@PathVariable String ifscCode) {
        boolean exists = branchService.existsByIfscCode(ifscCode);
        return ResponseEntity.ok(new ApiResponse<>(true, "IFSC code validation completed", exists));
    }

    @GetMapping("/active/{branchCode}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> isBranchActive(@PathVariable String branchCode) {
        boolean isActive = branchService.isBranchActive(branchCode);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branch status check completed", isActive));
    }

    // Statistics - Admin Only
    @GetMapping("/stats/total-count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getTotalBranchCount() {
        long count = branchService.getTotalBranchCount();
        return ResponseEntity.ok(new ApiResponse<>(true, "Total branch count retrieved successfully", count));
    }

    @GetMapping("/stats/active-count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getActiveBranchCount() {
        long count = branchService.getActiveBranchCount();
        return ResponseEntity.ok(new ApiResponse<>(true, "Active branch count retrieved successfully", count));
    }

    @GetMapping("/stats/city-count/{city}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getBranchCountByCity(@PathVariable String city) {
        long count = branchService.getBranchCountByCity(city);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branch count by city retrieved successfully", count));
    }

    // Utility Operations - Admin Only
    @GetMapping("/generate/branch-code")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> generateBranchCode(
            @RequestParam String city,
            @RequestParam String state) {
        String branchCode = branchService.generateBranchCode(city, state);
        return ResponseEntity.ok(new ApiResponse<>(true, "Branch code generated successfully", branchCode));
    }

    @GetMapping("/generate/ifsc-code/{branchCode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> generateIfscCode(@PathVariable String branchCode) {
        String ifscCode = branchService.generateIfscCode(branchCode);
        return ResponseEntity.ok(new ApiResponse<>(true, "IFSC code generated successfully", ifscCode));
    }
}
