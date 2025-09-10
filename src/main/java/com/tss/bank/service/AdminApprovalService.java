package com.tss.bank.service;

import java.util.List;
import java.util.Optional;

import com.tss.bank.entity.AdminApproval;

public interface AdminApprovalService {
    
    AdminApproval save(AdminApproval adminApproval);
    
    Optional<AdminApproval> findById(Integer approvalId);
    
    List<AdminApproval> findByUserId(Integer userId);
    
    List<AdminApproval> findByAdminId(Integer adminId);
    
    List<AdminApproval> findByAction(AdminApproval.Action action);
    
    List<AdminApproval> findByUserIdAndAction(Integer userId, AdminApproval.Action action);
    
    Long countApprovalsByAdmin(Integer adminId);
    
    List<AdminApproval> findAll();
    
    void deleteById(Integer approvalId);
    
    AdminApproval update(AdminApproval adminApproval);
}
