package com.tss.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tss.bank.entity.AdminApproval;

public interface AdminApprovalRepository extends JpaRepository<AdminApproval, Integer> {

    List<AdminApproval> findByUserId(Integer userId);
    
    List<AdminApproval> findByAdminId(Integer adminId);
    
    List<AdminApproval> findByAction(AdminApproval.Action action);
    
    List<AdminApproval> findByUserIdAndAction(Integer userId, AdminApproval.Action action);
    
    Long countByAdminId(Integer adminId);
}
