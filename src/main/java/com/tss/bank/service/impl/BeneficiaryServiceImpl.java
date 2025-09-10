package com.tss.bank.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.bank.dto.request.BeneficiaryRequest;
import com.tss.bank.dto.response.BeneficiaryResponse;
import com.tss.bank.entity.Account;
import com.tss.bank.entity.Beneficiary;
import com.tss.bank.exception.AccountApiException;
import com.tss.bank.exception.BeneficiaryApiException;
import com.tss.bank.repository.AccountRepository;
import com.tss.bank.repository.BeneficiaryRepository;
import com.tss.bank.service.BeneficiaryService;
import com.tss.bank.service.MappingService;

@Service
@Transactional
public class BeneficiaryServiceImpl implements BeneficiaryService {

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private MappingService mappingService;
    
    private static final int MAX_BENEFICIARIES_PER_ACCOUNT = 50;

    @Override
    public BeneficiaryResponse addBeneficiary(BeneficiaryRequest request) {
        // Validate account exists
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountApiException("Account not found with ID: " + request.getAccountId()));
        
        // Check if beneficiary already exists
        if (beneficiaryRepository.existsByAccountAccountIdAndBeneficiaryAccountNumber(
                request.getAccountId(), request.getBeneficiaryAccountNumber())) {
            throw new BeneficiaryApiException("Beneficiary with account number " + 
                    request.getBeneficiaryAccountNumber() + " already exists");
        }
        
        // Check maximum beneficiaries limit
        long beneficiaryCount = beneficiaryRepository.countByAccountAccountId(request.getAccountId());
        if (beneficiaryCount >= MAX_BENEFICIARIES_PER_ACCOUNT) {
            throw new BeneficiaryApiException("Maximum " + MAX_BENEFICIARIES_PER_ACCOUNT + 
                    " beneficiaries allowed per account");
        }
        
        // Validate beneficiary account number format
        if (!isValidAccountNumber(request.getBeneficiaryAccountNumber())) {
            throw new BeneficiaryApiException("Invalid beneficiary account number format");
        }
        
        // Validate IFSC code format
        if (!isValidIFSCCode(request.getIfscCode())) {
            throw new BeneficiaryApiException("Invalid IFSC code format");
        }
        
        // Create beneficiary
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setAccount(account);
        beneficiary.setBeneficiaryName(request.getBeneficiaryName());
        beneficiary.setBeneficiaryAccountNumber(request.getBeneficiaryAccountNumber());
        beneficiary.setIfscCode(request.getIfscCode());
        beneficiary.setBankName(request.getBankName());
        beneficiary.setBranchName(request.getBranchName());
        beneficiary.setCreatedDate(new java.util.Date());
        beneficiary.setUser(account.getUser());
        beneficiary.setIsActive(true);
        
        Beneficiary savedBeneficiary = beneficiaryRepository.save(beneficiary);
        return mappingService.map(savedBeneficiary, BeneficiaryResponse.class);
    }

    @Override
    public BeneficiaryResponse updateBeneficiary(Integer beneficiaryId, BeneficiaryRequest request) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new BeneficiaryApiException("Beneficiary not found with ID: " + beneficiaryId));
        
        // Validate account ownership
        if (!beneficiary.getAccount().getAccountId().equals(request.getAccountId())) {
            throw new BeneficiaryApiException("Cannot update beneficiary for different account");
        }
        
        // Check if new account number conflicts with existing beneficiary (excluding current one)
        if (!beneficiary.getBeneficiaryAccountNumber().equals(request.getBeneficiaryAccountNumber())) {
            if (beneficiaryRepository.existsByAccountAccountIdAndBeneficiaryAccountNumber(
                    request.getAccountId(), request.getBeneficiaryAccountNumber())) {
                throw new BeneficiaryApiException("Beneficiary with account number " + 
                        request.getBeneficiaryAccountNumber() + " already exists");
            }
        }
        
        // Validate formats
        if (!isValidAccountNumber(request.getBeneficiaryAccountNumber())) {
            throw new BeneficiaryApiException("Invalid beneficiary account number format");
        }
        
        if (!isValidIFSCCode(request.getIfscCode())) {
            throw new BeneficiaryApiException("Invalid IFSC code format");
        }
        
        // Update beneficiary details
        beneficiary.setBeneficiaryName(request.getBeneficiaryName());
        beneficiary.setBeneficiaryAccountNumber(request.getBeneficiaryAccountNumber());
        beneficiary.setIfscCode(request.getIfscCode());
        beneficiary.setBankName(request.getBankName());
        beneficiary.setBranchName(request.getBranchName());
        
        Beneficiary updatedBeneficiary = beneficiaryRepository.save(beneficiary);
        return mappingService.map(updatedBeneficiary, BeneficiaryResponse.class);
    }

    @Override
    public void deleteBeneficiary(Integer beneficiaryId, Integer accountId) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new BeneficiaryApiException("Beneficiary not found with ID: " + beneficiaryId));
        
        // Validate account ownership
        if (!beneficiary.getAccount().getAccountId().equals(accountId)) {
            throw new BeneficiaryApiException("Cannot delete beneficiary for different account");
        }
        
        beneficiaryRepository.delete(beneficiary);
    }

    @Override
    public BeneficiaryResponse getBeneficiaryDetails(Integer beneficiaryId) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new BeneficiaryApiException("Beneficiary not found with ID: " + beneficiaryId));
        return mappingService.map(beneficiary, BeneficiaryResponse.class);
    }

    @Override
    public List<BeneficiaryResponse> getAccountBeneficiaries(Integer accountId) {
        // Validate account exists
        if (!accountRepository.existsById(accountId)) {
            throw new AccountApiException("Account not found with ID: " + accountId);
        }
        
        List<Beneficiary> beneficiaries = beneficiaryRepository.findByAccountAccountId(accountId);
        return beneficiaries.stream()
                .map(beneficiary -> mappingService.map(beneficiary, BeneficiaryResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateBeneficiary(Integer accountId, String beneficiaryAccountNumber) {
        return beneficiaryRepository.existsByAccountAccountIdAndBeneficiaryAccountNumber(
                accountId, beneficiaryAccountNumber);
    }

    @Override
    public Optional<BeneficiaryResponse> findBeneficiaryByAccountNumber(Integer accountId, String beneficiaryAccountNumber) {
        Optional<Beneficiary> beneficiaryOpt = beneficiaryRepository
                .findByAccountAccountIdAndBeneficiaryAccountNumber(accountId, beneficiaryAccountNumber);
        
        return beneficiaryOpt.map(beneficiary -> 
                mappingService.map(beneficiary, BeneficiaryResponse.class));
    }

    @Override
    public List<BeneficiaryResponse> searchBeneficiaries(Integer accountId, String searchTerm) {
        // Validate account exists
        if (!accountRepository.existsById(accountId)) {
            throw new AccountApiException("Account not found with ID: " + accountId);
        }
        
        List<Beneficiary> beneficiaries = beneficiaryRepository
                .findByAccountIdAndBeneficiaryNameContaining(accountId, searchTerm);
        
        return beneficiaries.stream()
                .map(beneficiary -> mappingService.map(beneficiary, BeneficiaryResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public long getBeneficiaryCount(Integer accountId) {
        return beneficiaryRepository.countByAccountAccountId(accountId);
    }

    @Override
    public boolean isMaxBeneficiariesReached(Integer accountId) {
        return getBeneficiaryCount(accountId) >= MAX_BENEFICIARIES_PER_ACCOUNT;
    }

    @Override
    public Optional<Beneficiary> findById(Integer beneficiaryId) {
        return beneficiaryRepository.findById(beneficiaryId);
    }

    @Override
    public List<Beneficiary> findByAccountId(Integer accountId) {
        return beneficiaryRepository.findByAccountAccountId(accountId);
    }

    @Override
    public List<Beneficiary> findByBeneficiaryName(String beneficiaryName) {
        return beneficiaryRepository.findByBeneficiaryName(beneficiaryName);
    }

    @Override
    public Page<BeneficiaryResponse> findAllBeneficiaries(Pageable pageable) {
        Page<Beneficiary> beneficiaryPage = beneficiaryRepository.findAll(pageable);
        List<BeneficiaryResponse> responses = beneficiaryPage.getContent().stream()
                .map(beneficiary -> mappingService.map(beneficiary, BeneficiaryResponse.class))
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, beneficiaryPage.getTotalElements());
    }

    @Override
    public long getTotalBeneficiaryCount() {
        return beneficiaryRepository.count();
    }

    @Override
    public Beneficiary save(Beneficiary beneficiary) {
        return beneficiaryRepository.save(beneficiary);
    }

    @Override
    public void deleteById(Integer beneficiaryId) {
        if (!beneficiaryRepository.existsById(beneficiaryId)) {
            throw new BeneficiaryApiException("Beneficiary not found with ID: " + beneficiaryId);
        }
        beneficiaryRepository.deleteById(beneficiaryId);
    }

    // Helper methods for validation
    private boolean isValidAccountNumber(String accountNumber) {
        // Account number should be 10-20 digits
        return accountNumber != null && 
               accountNumber.matches("\\d{10,20}");
    }

    private boolean isValidIFSCCode(String ifscCode) {
        // IFSC code format: 4 letters + 0 + 6 alphanumeric characters
        return ifscCode != null && 
               ifscCode.matches("^[A-Z]{4}0[A-Z0-9]{6}$");
    }
}
