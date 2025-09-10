package com.tss.bank.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tss.bank.dto.request.AdminLoginRequest;
import com.tss.bank.dto.response.AdminResponse;
import com.tss.bank.dto.response.UserResponse;
import com.tss.bank.entity.Admin;
import com.tss.bank.entity.User;

public interface AdminService {
    
    // Authentication
    AdminResponse authenticateAdmin(AdminLoginRequest request);
    boolean validateAdminCredentials(String username, String password);
    
    // User Management
    UserResponse approveUser(Integer userId, Integer adminId);
    UserResponse rejectUser(Integer userId, Integer adminId, String reason);
    UserResponse activateUser(Integer userId, Integer adminId);
    UserResponse deactivateUser(Integer userId, Integer adminId);
    
    // User Queries
    List<UserResponse> getPendingApprovals();
    Page<UserResponse> getPendingApprovalsPaginated(Pageable pageable);
    List<UserResponse> getUsersByStatus(User.Status status);
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getUserDetails(Integer userId);
    List<UserResponse> searchUsers(String searchTerm);
    
    // User Statistics
    long getTotalUserCount();
    long getActiveUserCount();
    long getPendingUserCount();
    long getUserCountByStatus(User.Status status);
    
    // User Account Management
    boolean lockUserAccount(Integer userId, Integer adminId);
    boolean unlockUserAccount(Integer userId, Integer adminId);
    boolean resetUserPassword(Integer userId, Integer adminId, String newPassword);
    
    // Admin CRUD Operations
    Admin save(Admin admin);
    Optional<Admin> findById(Integer adminId);
    Optional<Admin> findByUsername(String username);
    Optional<Admin> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<Admin> findAll();
    Page<Admin> findAll(Pageable pageable);
    void deleteById(Integer adminId);
}
