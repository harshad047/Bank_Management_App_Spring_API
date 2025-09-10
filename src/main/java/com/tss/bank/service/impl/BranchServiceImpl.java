package com.tss.bank.service.impl;

import com.tss.bank.dto.request.BranchRequest;
import com.tss.bank.dto.response.BranchResponse;
import com.tss.bank.entity.Branch;
import com.tss.bank.exception.BranchApiException;
import com.tss.bank.repository.BranchRepository;
import com.tss.bank.service.BranchService;
import com.tss.bank.service.MappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class BranchServiceImpl implements BranchService {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private MappingService mappingService;

    @Override
    public BranchResponse createBranch(BranchRequest request) {
        // Generate unique branch code and IFSC code automatically
        String branchCode = generateBranchCode(request.getCity(), request.getState());
        String ifscCode = generateIfscCode(branchCode);

        Branch branch = Branch.builder()
                .branchName(request.getBranchName())
                .branchCode(branchCode)
                .ifscCode(ifscCode)
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .pincode(request.getPincode())
                .address(request.getAddress())
                .managerName(request.getManagerName())
                .contactNumber(request.getContactNumber())
                .email(request.getEmail())
                .status(Branch.Status.ACTIVE)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        Branch savedBranch = branchRepository.save(branch);
        return mappingService.map(savedBranch, BranchResponse.class);
    }

    @Override
    public BranchResponse updateBranch(Integer branchId, BranchRequest request) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchApiException("Branch not found with ID: " + branchId));

        // Check if branch code is being changed and if it already exists
        if (!branch.getBranchCode().equals(request.getBranchCode()) && 
            branchRepository.existsByBranchCode(request.getBranchCode())) {
            throw new BranchApiException("Branch code already exists: " + request.getBranchCode());
        }

        // Check if IFSC code is being changed and if it already exists
        if (!branch.getIfscCode().equals(request.getIfscCode()) && 
            branchRepository.existsByIfscCode(request.getIfscCode())) {
            throw new BranchApiException("IFSC code already exists: " + request.getIfscCode());
        }

        branch.setBranchName(request.getBranchName());
        branch.setBranchCode(request.getBranchCode());
        branch.setIfscCode(request.getIfscCode());
        branch.setCity(request.getCity());
        branch.setState(request.getState());
        branch.setCountry(request.getCountry());
        branch.setPincode(request.getPincode());
        branch.setAddress(request.getAddress());
        branch.setManagerName(request.getManagerName());
        branch.setContactNumber(request.getContactNumber());
        branch.setEmail(request.getEmail());
        branch.setUpdatedAt(new Date());

        Branch updatedBranch = branchRepository.save(branch);
        return mappingService.map(updatedBranch, BranchResponse.class);
    }

    @Override
    public BranchResponse getBranchDetails(Integer branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchApiException("Branch not found with ID: " + branchId));
        return mappingService.map(branch, BranchResponse.class);
    }

    @Override
    public BranchResponse getBranchByCode(String branchCode) {
        Branch branch = branchRepository.findByBranchCode(branchCode)
                .orElseThrow(() -> new BranchApiException("Branch not found with code: " + branchCode));
        return mappingService.map(branch, BranchResponse.class);
    }

    @Override
    public BranchResponse getBranchByIfsc(String ifscCode) {
        Branch branch = branchRepository.findByIfscCode(ifscCode)
                .orElseThrow(() -> new BranchApiException("Branch not found with IFSC: " + ifscCode));
        return mappingService.map(branch, BranchResponse.class);
    }

    @Override
    public void deleteBranch(Integer branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchApiException("Branch not found with ID: " + branchId));
        
        // Check if branch has associated users or accounts
        if (!branch.getUsers().isEmpty() || !branch.getAccounts().isEmpty()) {
            throw new BranchApiException("Cannot delete branch with existing users or accounts");
        }
        
        branchRepository.delete(branch);
    }

    @Override
    public List<BranchResponse> getAllBranches() {
        List<Branch> branches = branchRepository.findAll();
        return mappingService.mapList(branches, BranchResponse.class);
    }

    @Override
    public Page<BranchResponse> getAllBranches(Pageable pageable) {
        Page<Branch> branches = branchRepository.findAll(pageable);
        return branches.map(branch -> mappingService.map(branch, BranchResponse.class));
    }

    @Override
    public List<BranchResponse> getBranchesByStatus(Branch.Status status) {
        List<Branch> branches = branchRepository.findByStatus(status);
        return mappingService.mapList(branches, BranchResponse.class);
    }

    @Override
    public List<BranchResponse> getBranchesByCity(String city) {
        List<Branch> branches = branchRepository.findByCity(city);
        return mappingService.mapList(branches, BranchResponse.class);
    }

    @Override
    public List<BranchResponse> getBranchesByState(String state) {
        List<Branch> branches = branchRepository.findByState(state);
        return mappingService.mapList(branches, BranchResponse.class);
    }

    @Override
    public List<BranchResponse> getBranchesByCityAndState(String city, String state) {
        List<Branch> branches = branchRepository.findByCityAndState(city, state);
        return mappingService.mapList(branches, BranchResponse.class);
    }

    @Override
    public List<BranchResponse> searchBranchesByName(String name) {
        List<Branch> branches = branchRepository.findByBranchNameContaining(name);
        return mappingService.mapList(branches, BranchResponse.class);
    }

    @Override
    public BranchResponse activateBranch(Integer branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchApiException("Branch not found with ID: " + branchId));
        
        branch.setStatus(Branch.Status.ACTIVE);
        branch.setUpdatedAt(new Date());
        
        Branch updatedBranch = branchRepository.save(branch);
        return mappingService.map(updatedBranch, BranchResponse.class);
    }

    @Override
    public BranchResponse deactivateBranch(Integer branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchApiException("Branch not found with ID: " + branchId));
        
        branch.setStatus(Branch.Status.INACTIVE);
        branch.setUpdatedAt(new Date());
        
        Branch updatedBranch = branchRepository.save(branch);
        return mappingService.map(updatedBranch, BranchResponse.class);
    }

    @Override
    public BranchResponse closeBranch(Integer branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchApiException("Branch not found with ID: " + branchId));
        
        // Check if branch has active users or accounts
        if (!branch.getUsers().isEmpty() || !branch.getAccounts().isEmpty()) {
            throw new BranchApiException("Cannot close branch with existing users or accounts");
        }
        
        branch.setStatus(Branch.Status.CLOSED);
        branch.setUpdatedAt(new Date());
        
        Branch updatedBranch = branchRepository.save(branch);
        return mappingService.map(updatedBranch, BranchResponse.class);
    }

    @Override
    public boolean existsByBranchCode(String branchCode) {
        return branchRepository.existsByBranchCode(branchCode);
    }

    @Override
    public boolean existsByIfscCode(String ifscCode) {
        return branchRepository.existsByIfscCode(ifscCode);
    }

    @Override
    public boolean isBranchActive(String branchCode) {
        return branchRepository.findByBranchCode(branchCode)
                .map(branch -> branch.getStatus() == Branch.Status.ACTIVE)
                .orElse(false);
    }

    @Override
    public long getTotalBranchCount() {
        return branchRepository.count();
    }

    @Override
    public long getActiveBranchCount() {
        return branchRepository.countByStatus(Branch.Status.ACTIVE);
    }

    @Override
    public long getBranchCountByCity(String city) {
        return branchRepository.countByCity(city);
    }

    @Override
    public String generateBranchCode(String city, String state) {
        // Format: BR + City(2 chars) + State(2 chars) + 3 digit number
        String cityCode = city.substring(0, Math.min(2, city.length())).toUpperCase();
        String stateCode = state.substring(0, Math.min(2, state.length())).toUpperCase();
        
        // Get next sequential number
        long branchCount = branchRepository.count();
        String sequentialNumber = String.format("%03d", (branchCount + 1));
        
        String branchCode = "BR" + cityCode + stateCode + sequentialNumber;
        
        // Ensure uniqueness
        while (branchRepository.existsByBranchCode(branchCode)) {
            branchCount++;
            sequentialNumber = String.format("%03d", (branchCount + 1));
            branchCode = "BR" + cityCode + stateCode + sequentialNumber;
        }
        
        return branchCode;
    }

    @Override
    public String generateIfscCode(String branchCode) {
        // Format: BANK + 0 + BranchCode (11 characters total)
        // Example: BANK0BRMUMA001
        String bankCode = "BANK";
        String ifscCode = bankCode + "0" + branchCode;
        
        // Ensure IFSC is exactly 11 characters
        if (ifscCode.length() > 11) {
            ifscCode = ifscCode.substring(0, 11);
        } else if (ifscCode.length() < 11) {
            // Pad with zeros if needed
            ifscCode = ifscCode + "0".repeat(11 - ifscCode.length());
        }
        
        // Ensure uniqueness
        while (branchRepository.existsByIfscCode(ifscCode)) {
            Random random = new Random();
            String suffix = String.format("%02d", random.nextInt(100));
            String baseCode = bankCode + "0" + branchCode.substring(0, Math.min(6, branchCode.length()));
            ifscCode = (baseCode + suffix).substring(0, 11);
        }
        
        return ifscCode;
    }
}
