package com.tss.bank.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tss.bank.dto.request.BeneficiaryRequest;
import com.tss.bank.dto.response.BeneficiaryResponse;
import com.tss.bank.entity.Beneficiary;

public interface BeneficiaryService {
    
    // Main operations
    BeneficiaryResponse addBeneficiary(BeneficiaryRequest request);
    BeneficiaryResponse updateBeneficiary(Integer beneficiaryId, BeneficiaryRequest request);
    void deleteBeneficiary(Integer beneficiaryId, Integer accountId);
    BeneficiaryResponse getBeneficiaryDetails(Integer beneficiaryId);
    
    // Query operations
    List<BeneficiaryResponse> getAccountBeneficiaries(Integer accountId);
    boolean validateBeneficiary(Integer accountId, String beneficiaryAccountNumber);
    Optional<BeneficiaryResponse> findBeneficiaryByAccountNumber(Integer accountId, String beneficiaryAccountNumber);
    List<BeneficiaryResponse> searchBeneficiaries(Integer accountId, String searchTerm);
    
    // Statistics
    long getBeneficiaryCount(Integer accountId);
    boolean isMaxBeneficiariesReached(Integer accountId);
    long getTotalBeneficiaryCount();
    
    // Entity operations
    Beneficiary save(Beneficiary beneficiary);
    Optional<Beneficiary> findById(Integer beneficiaryId);
    List<Beneficiary> findByAccountId(Integer accountId);
    List<Beneficiary> findByBeneficiaryName(String beneficiaryName);
    Page<BeneficiaryResponse> findAllBeneficiaries(Pageable pageable);
    void deleteById(Integer beneficiaryId);
}
