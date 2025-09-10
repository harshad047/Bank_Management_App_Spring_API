package com.tss.bank.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tss.bank.entity.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Integer> {

    // Find transfers by from account ID
    List<Transfer> findByFromAccountIdOrderByTransferTimeDesc(Integer fromAccountId);
    
    // Find transfers by to account number
    List<Transfer> findByToAccountNumberOrderByTransferTimeDesc(String toAccountNumber);
    
    // Find transfers by from account ID with pagination
    Page<Transfer> findByFromAccountIdOrderByTransferTimeDesc(Integer fromAccountId, Pageable pageable);
    
    // Find transfers by status
    List<Transfer> findByStatus(Transfer.Status status);
    
    Page<Transfer> findByStatus(Transfer.Status status, Pageable pageable);
    
    // Find transfers by account ID (both from and to) with date range
    @Query("SELECT t FROM Transfer t WHERE (t.fromAccountId = :accountId OR t.toAccountId = :accountId) AND t.transferTime BETWEEN :fromDate AND :toDate ORDER BY t.transferTime DESC")
    List<Transfer> findByAccountIdAndTransferTimeBetween(@Param("accountId") Integer accountId,
                                                        @Param("fromDate") Date fromDate,
                                                        @Param("toDate") Date toDate);
    
    // Find transfers by from/to account ID for history
    @Query("SELECT t FROM Transfer t WHERE (t.fromAccountId = :fromAccountId OR t.toAccountId = :toAccountId) ORDER BY t.transferTime DESC")
    List<Transfer> findByFromAccountIdOrToAccountIdOrderByTransferTimeDesc(@Param("fromAccountId") Integer fromAccountId,
                                                                           @Param("toAccountId") Integer toAccountId);
    
    // Find transfers by from/to account ID with pagination
    @Query("SELECT t FROM Transfer t WHERE (t.fromAccountId = :fromAccountId OR t.toAccountId = :toAccountId)")
    Page<Transfer> findByFromAccountIdOrToAccountId(@Param("fromAccountId") Integer fromAccountId,
                                                   @Param("toAccountId") Integer toAccountId,
                                                   Pageable pageable);
    
    // Get total transferred amount by account and date range
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transfer t WHERE t.fromAccountId = :accountId AND t.status = 'COMPLETED' AND t.transferTime BETWEEN :fromDate AND :toDate")
    BigDecimal getTotalTransferredAmountByAccountAndDateRange(@Param("accountId") Integer accountId,
                                                             @Param("fromDate") Date fromDate,
                                                             @Param("toDate") Date toDate);
    
    // Find high value transfers
    @Query("SELECT t FROM Transfer t WHERE t.amount >= :threshold AND t.status = 'COMPLETED' ORDER BY t.amount DESC")
    List<Transfer> findHighValueTransfers(@Param("threshold") BigDecimal threshold);
    
    // Find transfers by amount greater than threshold
    List<Transfer> findByAmountGreaterThan(BigDecimal threshold);
    
    // Count transfers by account ID and date range
    @Query("SELECT COUNT(t) FROM Transfer t WHERE (t.fromAccountId = :accountId OR t.toAccountId = :accountId) AND t.transferTime BETWEEN :fromDate AND :toDate")
    long countByAccountIdAndTransferTimeBetween(@Param("accountId") Integer accountId,
                                               @Param("fromDate") Date fromDate,
                                               @Param("toDate") Date toDate);
    
    // Count by status
    long countByStatus(Transfer.Status status);
    
    // Check if account number exists
    boolean existsByToAccountNumber(String accountNumber);
}
