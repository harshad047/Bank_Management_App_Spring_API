package com.tss.bank.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tss.bank.entity.FDApplication;

public interface FDApplicationRepository extends JpaRepository<FDApplication, Integer> {

    List<FDApplication> findByUserId(Integer userId);
    
    List<FDApplication> findByStatus(FDApplication.Status status);
    
    List<FDApplication> findByApprovedBy(Integer approvedBy);
    
    List<FDApplication> findByUserIdAndStatus(Integer userId, FDApplication.Status status);
    
    List<FDApplication> findByAmountGreaterThanEqual(BigDecimal minAmount);
    
    Long countByStatus(FDApplication.Status status);
}
