package com.tss.bank.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tss.bank.dto.request.FixedDepositRequest;
import com.tss.bank.dto.response.FixedDepositResponse;
import com.tss.bank.entity.FixedDeposit;

public interface FixedDepositService {
    
    // DTO-based operations
    FixedDepositResponse createFixedDeposit(FixedDepositRequest request);
    FixedDepositResponse getFixedDepositDetails(Integer fdId);
    List<FixedDepositResponse> getAccountFixedDeposits(Integer accountId);
    List<FixedDepositResponse> getUserFixedDeposits(Integer userId);
    FixedDepositResponse prematureWithdrawal(Integer fdId, String reason);
    FixedDepositResponse matureFixedDeposit(Integer fdId);
    
    // Business logic methods
    BigDecimal calculateInterestRate(Integer tenureMonths);
    BigDecimal calculateMaturityAmount(BigDecimal principal, BigDecimal interestRate, Integer tenureMonths);
    Date calculateMaturityDate(Date startDate, Integer tenureMonths);
    
    // Maturity processing
    List<FixedDepositResponse> getMaturedDeposits();
    void processMaturedDeposits();
    
    // Analytics
    BigDecimal getTotalActiveDeposits(Integer accountId);
    Page<FixedDepositResponse> findAllFixedDeposits(Pageable pageable);
    long getTotalFixedDepositCount();
    long getActiveFixedDepositCount();
    long getMaturedFixedDepositCount();
    
    // Entity-based operations (for backward compatibility)
    FixedDeposit save(FixedDeposit fixedDeposit);
    Optional<FixedDeposit> findById(Integer fdId);
    List<FixedDeposit> findByUserId(Integer userId);
    List<FixedDeposit> findByAccountId(Integer accountId);
    List<FixedDeposit> findByStatus(FixedDeposit.Status status);
    List<FixedDeposit> findByUserIdAndStatus(Integer userId, FixedDeposit.Status status);
    List<FixedDeposit> findMaturedDeposits(Date date);
    BigDecimal getTotalAmountByUserAndStatus(Integer userId, FixedDeposit.Status status);
    List<FixedDeposit> findAll();
    void deleteById(Integer fdId);
    FixedDeposit update(FixedDeposit fixedDeposit);
}
