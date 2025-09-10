package com.tss.bank.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tss.bank.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<Account> findByAccountNumber(String accountNumber);
    
    List<Account> findByUserUserId(Integer userId);
    
    boolean existsByAccountNumber(String accountNumber);
    
    boolean existsByAccountIdAndUserUserId(Integer accountId, Integer userId);
    
    List<Account> findByBalanceGreaterThanEqual(BigDecimal balance);
    
    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.user.userId = :userId")
    BigDecimal getTotalBalanceByUserId(@Param("userId") Integer userId);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.balance >= :minBalance")
    long countByBalanceGreaterThanEqual(@Param("minBalance") BigDecimal minBalance);
    
    @Query("SELECT a FROM Account a WHERE a.user.userId = :userId AND a.balance >= :minBalance")
    List<Account> findByUserIdAndMinBalance(@Param("userId") Integer userId, @Param("minBalance") BigDecimal minBalance);
}
