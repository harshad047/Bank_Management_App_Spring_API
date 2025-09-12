package com.tss.bank.service.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.bank.dto.request.PasswordChangeRequest;
import com.tss.bank.dto.request.UserLoginRequest;
import com.tss.bank.dto.request.UserProfileUpdateRequest;
import com.tss.bank.dto.request.UserRegistrationRequest;
import com.tss.bank.dto.response.UserResponse;
import com.tss.bank.entity.Branch;
import com.tss.bank.entity.User;
import com.tss.bank.exception.UserApiException;
import com.tss.bank.repository.BranchRepository;
import com.tss.bank.repository.UserRepository;
import com.tss.bank.service.MappingService;
import com.tss.bank.service.UserService;
import com.tss.bank.service.EmailService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MappingService mappingService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public UserResponse registerUser(UserRegistrationRequest request) {
        // Validate date of birth - must not be today or future
        validateDateOfBirth(request.getDateOfBirth());
        
        // Check if username already exists
        if (existsByUsername(request.getUsername())) {
            throw new UserApiException("Username already exists: " + request.getUsername());
        }
        
        // Check if email already exists
        if (existsByEmail(request.getEmail())) {
            throw new UserApiException("Email already exists: " + request.getEmail());
        }
        
        // Check if phone already exists
        if (request.getPhone() != null && existsByPhone(request.getPhone())) {
            throw new UserApiException("Phone number already exists: " + request.getPhone());
        }
        
        // Validate branch code exists and is active
        Branch branch = validateAndGetBranch(request.getBranchCode());
        
        // Encode password
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        // Create user entity
        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .branch(branch)
                .status(User.Status.PENDING)
                .role(User.Role.USER)
                .createdAt(new Date(System.currentTimeMillis()))
                .build();
        
        User savedUser = userRepository.save(user);
        return mappingService.map(savedUser, UserResponse.class);
    }

    @Override
    public UserResponse authenticateUser(UserLoginRequest request) {
        User user = findByUsername(request.getUsername())
                .orElseThrow(() -> new UserApiException("Invalid username or password"));
        
        if (!validateCredentials(request.getUsername(), request.getPassword())) {
            throw new UserApiException("Invalid username or password");
        }
        
        if (user.getStatus() != User.Status.ACTIVE) {
            throw new UserApiException("Account is not active. Status: " + user.getStatus());
        }
        
        return mappingService.map(user, UserResponse.class);
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    @Override
    public UserResponse updateProfile(Integer userId, UserProfileUpdateRequest request) {
        User user = findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        
        // Update only non-null fields
        if (request.getEmail() != null) {
            if (!request.getEmail().equals(user.getEmail()) && existsByEmail(request.getEmail())) {
                throw new UserApiException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getPhone() != null) {
            if (!request.getPhone().equals(user.getPhone()) && existsByPhone(request.getPhone())) {
                throw new UserApiException("Phone number already exists: " + request.getPhone());
            }
            user.setPhone(request.getPhone());
        }
        
        User updatedUser = userRepository.save(user);
        return mappingService.map(updatedUser, UserResponse.class);
    }

    @Override
    public boolean changePassword(Integer userId, PasswordChangeRequest request) {
        User user = findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        
        // Validate current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UserApiException("Current password is incorrect");
        }
        
        // Validate new password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new UserApiException("New password and confirm password do not match");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        return true;
    }

    @Override
    public boolean resetPassword(String email) {
        User user = findByEmail(email)
                .orElseThrow(() -> new UserApiException("User not found with email: " + email));
        
        // Generate temporary password (in real implementation, send via email)
        String tempPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);
        
        // TODO: Send email with temporary password
        return true;
    }

    @Override
    public UserResponse approveUser(Integer userId, Integer adminId) {
        User user = findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        
        if (user.getStatus() != User.Status.PENDING) {
            throw new UserApiException("User is not in pending status");
        }
        
        user.setStatus(User.Status.ACTIVE);
        user.setApprovedBy(adminId);
        user.setApprovedAt(Date.valueOf(LocalDate.now()));
        user.setRejectionReason(null);
        
        User approvedUser = userRepository.save(user);
        
        // Send approval email notification
        try {
            String userName = (user.getFirstName() != null ? user.getFirstName() : "") + 
                            (user.getLastName() != null ? " " + user.getLastName() : "");
            if (userName.trim().isEmpty()) {
                userName = user.getUsername();
            }
            emailService.sendUserApprovalEmail(user.getEmail(), userName.trim());
        } catch (Exception e) {
            // Log the error but don't fail the approval process
            System.err.println("Failed to send approval email: " + e.getMessage());
        }
        
        return mappingService.map(approvedUser, UserResponse.class);
    }

    @Override
    public UserResponse rejectUser(Integer userId, Integer adminId, String reason) {
        User user = findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        
        if (user.getStatus() != User.Status.PENDING) {
            throw new UserApiException("User is not in pending status");
        }
        
        user.setStatus(User.Status.REJECTED);
        user.setApprovedBy(adminId);
        user.setApprovedAt(Date.valueOf(LocalDate.now()));
        user.setRejectionReason(reason);
        
        User rejectedUser = userRepository.save(user);
        
        // Send rejection email notification
        try {
            String userName = (user.getFirstName() != null ? user.getFirstName() : "") + 
                            (user.getLastName() != null ? " " + user.getLastName() : "");
            if (userName.trim().isEmpty()) {
                userName = user.getUsername();
            }
            emailService.sendUserRejectionEmail(user.getEmail(), userName.trim(), reason);
        } catch (Exception e) {
            // Log the error but don't fail the rejection process
            System.err.println("Failed to send rejection email: " + e.getMessage());
        }
        
        return mappingService.map(rejectedUser, UserResponse.class);
    }

    @Override
    public UserResponse activateUser(Integer userId) {
        User user = findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        
        user.setStatus(User.Status.ACTIVE);
        User activatedUser = userRepository.save(user);
        return mappingService.map(activatedUser, UserResponse.class);
    }

    @Override
    public UserResponse deactivateUser(Integer userId) {
        User user = findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        
        user.setStatus(User.Status.INACTIVE);
        User deactivatedUser = userRepository.save(user);
        return mappingService.map(deactivatedUser, UserResponse.class);
    }

    @Override
    public boolean verifySecurityAnswers(Integer userId, List<String> answers) {
        // TODO: Implement security question verification
        return true;
    }

    @Override
    public void lockAccount(Integer userId) {
        User user = findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        
        user.setStatus(User.Status.INACTIVE);
        userRepository.save(user);
    }

    @Override
    public void unlockAccount(Integer userId) {
        User user = findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        
        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Integer userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public List<UserResponse> findByStatus(User.Status status) {
        List<User> users = userRepository.findByStatus(status);
        return mappingService.mapList(users, UserResponse.class);
    }

    @Override
    public Page<UserResponse> findByStatusPaginated(User.Status status, Pageable pageable) {
        Page<User> users = userRepository.findByStatus(status, pageable);
        return users.map(user -> mappingService.map(user, UserResponse.class));
    }

    @Override
    public List<UserResponse> findPendingApprovals() {
        return findByStatus(User.Status.PENDING);
    }

    @Override
    public Page<UserResponse> findAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(user -> mappingService.map(user, UserResponse.class));
    }

    @Override
    public UserResponse getUserById(Integer userId) {
        User user = findById(userId)
                .orElseThrow(() -> new UserApiException("User not found with ID: " + userId));
        return mappingService.map(user, UserResponse.class);
    }

    @Override
    public long getTotalUserCount() {
        return userRepository.count();
    }

    @Override
    public long getActiveUserCount() {
        return userRepository.countByStatus(User.Status.ACTIVE);
    }

    @Override
    public long getPendingUserCount() {
        return userRepository.countByStatus(User.Status.PENDING);
    }
    
    private String generateTemporaryPassword() {
        // Generate a random 8-character password
        return "Temp" + System.currentTimeMillis() % 10000;
    }
    
    /**
     * Validates date of birth - must not be today or in the future, and user must be at least 18 years old
     */
    private void validateDateOfBirth(Date dateOfBirth) {
        if (dateOfBirth == null) {
            throw new UserApiException("Date of birth is required");
        }
        
        LocalDate dob = dateOfBirth.toLocalDate();
        LocalDate today = LocalDate.now();
        
        // Check if DOB is today or in the future
        if (!dob.isBefore(today)) {
            throw new UserApiException("Date of birth cannot be today or in the future");
        }
        
        // Check minimum age (18 years)
        Period age = Period.between(dob, today);
        if (age.getYears() < 18) {
            throw new UserApiException("User must be at least 18 years old");
        }
        
        // Check maximum age (100 years)
        if (age.getYears() > 100) {
            throw new UserApiException("User age cannot exceed 100 years");
        }
    }
    
    /**
     * Validates branch code exists and is active
     */
    private Branch validateAndGetBranch(String branchCode) {
        if (branchCode == null || branchCode.trim().isEmpty()) {
            throw new UserApiException("Branch code is required");
        }
        
        Branch branch = branchRepository.findByBranchCode(branchCode)
                .orElseThrow(() -> new UserApiException("Branch not found with code: " + branchCode));
        
        if (branch.getStatus() != Branch.Status.ACTIVE) {
            throw new UserApiException("Branch with code " + branchCode + " is not active");
        }
        
        return branch;
    }
}
