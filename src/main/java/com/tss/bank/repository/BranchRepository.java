package com.tss.bank.repository;

import com.tss.bank.entity.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {
    
    Optional<Branch> findByBranchCode(String branchCode);
    
    Optional<Branch> findByIfscCode(String ifscCode);
    
    List<Branch> findByStatus(Branch.Status status);
    
    List<Branch> findByCity(String city);
    
    List<Branch> findByState(String state);
    
    List<Branch> findByCityAndState(String city, String state);
    
    Page<Branch> findByStatus(Branch.Status status, Pageable pageable);
    
    @Query("SELECT b FROM Branch b WHERE b.branchName LIKE %:name%")
    List<Branch> findByBranchNameContaining(@Param("name") String name);
    
    @Query("SELECT b FROM Branch b WHERE b.city = :city AND b.status = :status")
    List<Branch> findByCityAndStatus(@Param("city") String city, @Param("status") Branch.Status status);
    
    boolean existsByBranchCode(String branchCode);
    
    boolean existsByIfscCode(String ifscCode);
    
    long countByStatus(Branch.Status status);
    
    @Query("SELECT COUNT(b) FROM Branch b WHERE b.city = :city")
    long countByCity(@Param("city") String city);
}
