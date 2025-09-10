package com.tss.bank.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tss.bank.dto.request.UserRegistrationRequest;
import com.tss.bank.dto.request.UserLoginRequest;
import com.tss.bank.dto.request.PasswordChangeRequest;
import com.tss.bank.dto.request.UserProfileUpdateRequest;
import com.tss.bank.dto.response.UserResponse;
import com.tss.bank.entity.User;

public interface UserService {
    
    // Registration and Authentication
    UserResponse registerUser(UserRegistrationRequest request);
    UserResponse authenticateUser(UserLoginRequest request);
    boolean validateCredentials(String username, String password);
    
    // Profile Management
    UserResponse updateProfile(Integer userId, UserProfileUpdateRequest request);
    boolean changePassword(Integer userId, PasswordChangeRequest request);
    boolean resetPassword(String email);
    
    // User Status Management
    UserResponse approveUser(Integer userId, Integer adminId);
    UserResponse rejectUser(Integer userId, Integer adminId, String reason);
    UserResponse activateUser(Integer userId);
    UserResponse deactivateUser(Integer userId);
    
    // Security
    boolean verifySecurityAnswers(Integer userId, List<String> answers);
    void lockAccount(Integer userId);
    void unlockAccount(Integer userId);
    
    // Query Methods
    Optional<User> findById(Integer userId);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    
    List<UserResponse> findByStatus(User.Status status);
    Page<UserResponse> findByStatusPaginated(User.Status status, Pageable pageable);
    List<UserResponse> findPendingApprovals();
    
    // Admin Operations
    Page<UserResponse> findAllUsers(Pageable pageable);
    UserResponse getUserById(Integer userId);
    long getTotalUserCount();
    long getActiveUserCount();
    long getPendingUserCount();
}
