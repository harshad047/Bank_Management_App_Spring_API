package com.tss.bank.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.bank.entity.AdminApproval;
import com.tss.bank.repository.AdminApprovalRepository;
import com.tss.bank.service.AdminApprovalService;

@Service
@Transactional
public class AdminApprovalServiceImpl implements AdminApprovalService {

    @Autowired
    private AdminApprovalRepository adminApprovalRepository;

    @Override
    public AdminApproval save(AdminApproval adminApproval) {
        return adminApprovalRepository.save(adminApproval);
    }

    @Override
    public Optional<AdminApproval> findById(Integer approvalId) {
        return adminApprovalRepository.findById(approvalId);
    }

    @Override
    public List<AdminApproval> findByUserId(Integer userId) {
        return adminApprovalRepository.findByUserId(userId);
    }

    @Override
    public List<AdminApproval> findByAdminId(Integer adminId) {
        return adminApprovalRepository.findByAdminId(adminId);
    }

    @Override
    public List<AdminApproval> findByAction(AdminApproval.Action action) {
        return adminApprovalRepository.findByAction(action);
    }

    @Override
    public List<AdminApproval> findByUserIdAndAction(Integer userId, AdminApproval.Action action) {
        return adminApprovalRepository.findByUserIdAndAction(userId, action);
    }

    @Override
    public Long countApprovalsByAdmin(Integer adminId) {
        return adminApprovalRepository.countByAdminId(adminId);
    }

    @Override
    public List<AdminApproval> findAll() {
        return adminApprovalRepository.findAll();
    }

    @Override
    public void deleteById(Integer approvalId) {
        adminApprovalRepository.deleteById(approvalId);
    }

    @Override
    public AdminApproval update(AdminApproval adminApproval) {
        return adminApprovalRepository.save(adminApproval);
    }
}
