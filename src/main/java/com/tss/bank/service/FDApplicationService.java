package com.tss.bank.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.tss.bank.entity.FDApplication;

public interface FDApplicationService {
    
    FDApplication save(FDApplication fdApplication);
    
    Optional<FDApplication> findById(Integer fdAppId);
    
    List<FDApplication> findByUserId(Integer userId);
    
    List<FDApplication> findByStatus(FDApplication.Status status);
    
    List<FDApplication> findByApprovedBy(Integer approvedBy);
    
    List<FDApplication> findByUserIdAndStatus(Integer userId, FDApplication.Status status);
    
    List<FDApplication> findByMinAmount(BigDecimal minAmount);
    
    Long countPendingApplications();
    
    List<FDApplication> findAll();
    
    void deleteById(Integer fdAppId);
    
    FDApplication update(FDApplication fdApplication);
}
