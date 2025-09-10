package com.tss.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tss.bank.entity.Beneficiary;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Integer> {

    List<Beneficiary> findByAccountAccountId(Integer accountId);
    
    Optional<Beneficiary> findByAccountAccountIdAndBeneficiaryAccountNumber(Integer accountId, String beneficiaryAccountNumber);
    
    boolean existsByAccountAccountIdAndBeneficiaryAccountNumber(Integer accountId, String beneficiaryAccountNumber);
    
    List<Beneficiary> findByBeneficiaryName(String beneficiaryName);
    
    @Query("SELECT b FROM Beneficiary b WHERE b.account.accountId = :accountId AND b.beneficiaryName LIKE %:name%")
    List<Beneficiary> findByAccountIdAndBeneficiaryNameContaining(@Param("accountId") Integer accountId, @Param("name") String name);
    
    long countByAccountAccountId(Integer accountId);
}
