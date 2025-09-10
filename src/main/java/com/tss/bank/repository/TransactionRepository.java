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

import com.tss.bank.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByAccountAccountIdOrderByTxnTimeDesc(Integer accountId);
    
    Page<Transaction> findByAccountAccountIdOrderByTxnTimeDesc(Integer accountId, Pageable pageable);
    
    List<Transaction> findByAccountUserUserIdOrderByTxnTimeDesc(Integer userId);
    
    Page<Transaction> findByAccountUserUserIdOrderByTxnTimeDesc(Integer userId, Pageable pageable);
    
    List<Transaction> findByTxnType(Transaction.TxnType txnType);
    
    List<Transaction> findByChannel(Transaction.Channel channel);
    
    @Query("SELECT t FROM Transaction t WHERE t.account.accountId = :accountId AND t.txnTime BETWEEN :fromDate AND :toDate ORDER BY t.txnTime DESC")
    List<Transaction> findByAccountAndDateRange(@Param("accountId") Integer accountId, 
                                               @Param("fromDate") Date fromDate, 
                                               @Param("toDate") Date toDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account.accountId = :accountId AND t.txnType = :txnType AND t.txnTime BETWEEN :fromDate AND :toDate")
    BigDecimal getTotalAmountByAccountAndTypeAndDateRange(@Param("accountId") Integer accountId,
                                                         @Param("txnType") Transaction.TxnType txnType,
                                                         @Param("fromDate") Date fromDate,
                                                         @Param("toDate") Date toDate);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.account.accountId = :accountId AND t.txnTime BETWEEN :fromDate AND :toDate")
    long countByAccountAndDateRange(@Param("accountId") Integer accountId,
                                   @Param("fromDate") Date fromDate,
                                   @Param("toDate") Date toDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.amount >= :threshold ORDER BY t.amount DESC")
    List<Transaction> findHighValueTransactions(@Param("threshold") BigDecimal threshold);
    
    @Query("SELECT t FROM Transaction t WHERE t.txnType = 'DEBIT' AND t.amount >= :threshold AND t.txnTime >= :date")
    List<Transaction> findSuspiciousWithdrawals(@Param("threshold") BigDecimal threshold, @Param("date") Date date);
    
    // Additional methods needed by service implementation
    Page<Transaction> findByAccountAccountId(Integer accountId, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.account.accountId = :accountId AND t.txnTime BETWEEN :fromDate AND :toDate")
    Page<Transaction> findByAccountIdAndTxnTimeBetween(@Param("accountId") Integer accountId,
                                                      @Param("fromDate") Date fromDate,
                                                      @Param("toDate") Date toDate,
                                                      Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.account.accountId = :accountId AND t.txnTime BETWEEN :fromDate AND :toDate ORDER BY t.txnTime ASC")
    List<Transaction> findByAccountIdAndTxnTimeBetweenOrderByTxnTimeAsc(@Param("accountId") Integer accountId,
                                                                       @Param("fromDate") Date fromDate,
                                                                       @Param("toDate") Date toDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.account.accountId = :accountId AND t.txnTime > :date ORDER BY t.txnTime ASC")
    List<Transaction> findByAccountIdAndTxnTimeGreaterThanOrderByTxnTimeAsc(@Param("accountId") Integer accountId,
                                                                           @Param("date") Date date);
    
    @Query("SELECT t FROM Transaction t WHERE t.account.accountId = :accountId ORDER BY t.txnTime DESC LIMIT 1")
    Optional<Transaction> findTopByAccountAccountIdOrderByTxnTimeDesc(@Param("accountId") Integer accountId);
}
