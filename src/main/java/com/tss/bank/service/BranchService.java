package com.tss.bank.service;

import com.tss.bank.dto.request.BranchRequest;
import com.tss.bank.dto.response.BranchResponse;
import com.tss.bank.entity.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BranchService {
    
    // CRUD Operations
    BranchResponse createBranch(BranchRequest request);
    BranchResponse updateBranch(Integer branchId, BranchRequest request);
    BranchResponse getBranchDetails(Integer branchId);
    BranchResponse getBranchByCode(String branchCode);
    BranchResponse getBranchByIfsc(String ifscCode);
    void deleteBranch(Integer branchId);
    
    // Search and Filter Operations
    List<BranchResponse> getAllBranches();
    Page<BranchResponse> getAllBranches(Pageable pageable);
    List<BranchResponse> getBranchesByStatus(Branch.Status status);
    List<BranchResponse> getBranchesByCity(String city);
    List<BranchResponse> getBranchesByState(String state);
    List<BranchResponse> getBranchesByCityAndState(String city, String state);
    List<BranchResponse> searchBranchesByName(String name);
    
    // Status Management
    BranchResponse activateBranch(Integer branchId);
    BranchResponse deactivateBranch(Integer branchId);
    BranchResponse closeBranch(Integer branchId);
    
    // Validation Operations
    boolean existsByBranchCode(String branchCode);
    boolean existsByIfscCode(String ifscCode);
    boolean isBranchActive(String branchCode);
    
    // Statistics
    long getTotalBranchCount();
    long getActiveBranchCount();
    long getBranchCountByCity(String city);
    
    // Utility Methods
    String generateBranchCode(String city, String state);
    String generateIfscCode(String branchCode);
}
