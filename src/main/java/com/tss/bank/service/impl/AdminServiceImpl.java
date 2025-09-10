package com.tss.bank.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.bank.dto.request.AdminLoginRequest;
import com.tss.bank.dto.response.AdminResponse;
import com.tss.bank.dto.response.UserResponse;
import com.tss.bank.entity.Admin;
import com.tss.bank.entity.User;
import com.tss.bank.exception.AdminApiException;
import com.tss.bank.exception.UserApiException;
import com.tss.bank.repository.AdminRepository;
import com.tss.bank.repository.UserRepository;
import com.tss.bank.service.AdminService;
import com.tss.bank.service.MappingService;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MappingService mappingService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AdminResponse authenticateAdmin(AdminLoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AdminApiException("Invalid username or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new AdminApiException("Invalid username or password");
        }
        
        return mappingService.map(admin, AdminResponse.class);
    }

    @Override
    public boolean validateAdminCredentials(String username, String password) {
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            // Use plain text comparison since passwords are stored as plain text
            return password.equals(admin.getPassword());
        }
        return false;
    }

    @Override
    public UserResponse approveUser(Integer userId, Integer adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminApiException("Admin not found with ID: " + adminId));
        
        if (user.getStatus() != User.Status.PENDING) {
            throw new UserApiException("User is not in pending status");
        }
        
        user.setStatus(User.Status.ACTIVE);
        user.setApprovedBy(adminId);
        user.setApprovedAt(new java.sql.Date(System.currentTimeMillis()));
        
        User savedUser = userRepository.save(user);
        return mappingService.map(savedUser, UserResponse.class);
    }

    @Override
    public UserResponse rejectUser(Integer userId, Integer adminId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminApiException("Admin not found with ID: " + adminId));
        
        if (user.getStatus() != User.Status.PENDING) {
            throw new UserApiException("User is not in pending status");
        }
        
        user.setStatus(User.Status.REJECTED);
        user.setApprovedBy(adminId);
        user.setApprovedAt(new java.sql.Date(System.currentTimeMillis()));
        user.setRejectionReason(reason);
        
        User savedUser = userRepository.save(user);
        return mappingService.map(savedUser, UserResponse.class);
    }

    @Override
    public UserResponse activateUser(Integer userId, Integer adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminApiException("Admin not found with ID: " + adminId));
        
        if (user.getStatus() == User.Status.ACTIVE) {
            throw new UserApiException("User is already active");
        }
        
        user.setStatus(User.Status.ACTIVE);
        
        User savedUser = userRepository.save(user);
        return mappingService.map(savedUser, UserResponse.class);
    }

    @Override
    public UserResponse deactivateUser(Integer userId, Integer adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminApiException("Admin not found with ID: " + adminId));
        
        if (user.getStatus() == User.Status.INACTIVE) {
            throw new UserApiException("User is already inactive");
        }
        
        user.setStatus(User.Status.INACTIVE);
        
        User savedUser = userRepository.save(user);
        return mappingService.map(savedUser, UserResponse.class);
    }

    @Override
    public List<UserResponse> getPendingApprovals() {
        List<User> pendingUsers = userRepository.findByStatus(User.Status.PENDING);
        return pendingUsers.stream()
                .map(user -> mappingService.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserResponse> getPendingApprovalsPaginated(Pageable pageable) {
        Page<User> userPage = userRepository.findByStatus(User.Status.PENDING, pageable);
        List<UserResponse> responses = userPage.getContent().stream()
                .map(user -> mappingService.map(user, UserResponse.class))
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, userPage.getTotalElements());
    }

    @Override
    public List<UserResponse> getUsersByStatus(User.Status status) {
        List<User> users = userRepository.findByStatus(status);
        return users.stream()
                .map(user -> mappingService.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserResponse> responses = userPage.getContent().stream()
                .map(user -> mappingService.map(user, UserResponse.class))
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, userPage.getTotalElements());
    }

    @Override
    public UserResponse getUserDetails(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        return mappingService.map(user, UserResponse.class);
    }

    @Override
    public List<UserResponse> searchUsers(String searchTerm) {
        List<User> users = userRepository.findByUsernameContaining(searchTerm);
        return users.stream()
                .map(user -> mappingService.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public long getTotalUserCount() {
        return userRepository.count();
    }

    @Override
    public long getActiveUserCount() {
        return userRepository.countActiveUsers();
    }

    @Override
    public long getPendingUserCount() {
        return userRepository.countPendingUsers();
    }

    @Override
    public long getUserCountByStatus(User.Status status) {
        return userRepository.countByStatus(status);
    }

    @Override
    public boolean lockUserAccount(Integer userId, Integer adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminApiException("Admin not found with ID: " + adminId));
        
        // Note: User entity doesn't have locked field, using status instead
        user.setStatus(User.Status.INACTIVE);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean unlockUserAccount(Integer userId, Integer adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminApiException("Admin not found with ID: " + adminId));
        
        // Note: User entity doesn't have locked field, using status instead
        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean resetUserPassword(Integer userId, Integer adminId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminApiException("Admin not found with ID: " + adminId));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    public Optional<Admin> findById(Integer adminId) {
        return adminRepository.findById(adminId);
    }

    @Override
    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return adminRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return adminRepository.existsByEmail(email);
    }

    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    @Override
    public Page<Admin> findAll(Pageable pageable) {
        return adminRepository.findAll(pageable);
    }

    @Override
    public Admin save(Admin admin) {
        // Encode password if it's not already encoded
        if (admin.getPassword() != null && !admin.getPassword().startsWith("$2a$")) {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        }
        return adminRepository.save(admin);
    }

    @Override
    public void deleteById(Integer adminId) {
        if (!adminRepository.existsById(adminId)) {
            throw new AdminApiException("Admin not found with ID: " + adminId);
        }
        adminRepository.deleteById(adminId);
    }
}
