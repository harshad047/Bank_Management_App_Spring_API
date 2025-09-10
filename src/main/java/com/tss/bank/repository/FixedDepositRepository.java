package com.tss.bank.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tss.bank.entity.FixedDeposit;

public interface FixedDepositRepository extends JpaRepository<FixedDeposit, Integer> {

    List<FixedDeposit> findByAccountAccountId(Integer accountId);
    
    List<FixedDeposit> findByStatus(FixedDeposit.Status status);
    
    List<FixedDeposit> findByAccountUserUserId(Integer userId);
    
    @Query("SELECT fd FROM FixedDeposit fd WHERE fd.maturityDate <= :date AND fd.status = 'ACTIVE'")
    List<FixedDeposit> findMaturedDeposits(@Param("date") Date date);
    
    @Query("SELECT SUM(fd.amount) FROM FixedDeposit fd WHERE fd.account.accountId = :accountId AND fd.status = 'ACTIVE'")
    BigDecimal getTotalActiveDepositsByAccount(@Param("accountId") Integer accountId);
    
    @Query("SELECT COUNT(fd) FROM FixedDeposit fd WHERE fd.status = :status")
    long countByStatus(@Param("status") FixedDeposit.Status status);
    
    @Query("SELECT fd FROM FixedDeposit fd WHERE fd.tenureMonths = :tenure AND fd.status = 'ACTIVE'")
    List<FixedDeposit> findByTenure(@Param("tenure") Integer tenure);
}
