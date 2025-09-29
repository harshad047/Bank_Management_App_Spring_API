package com.tss.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tss.bank.dto.request.PasswordChangeRequest;
import com.tss.bank.dto.request.UserProfileUpdateRequest;
import com.tss.bank.dto.response.ApiResponse;
import com.tss.bank.dto.response.UserResponse;
import com.tss.bank.entity.User;
import com.tss.bank.service.UserService;
import com.tss.bank.service.AuthorizationService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthorizationService authorizationService;

    // Profile Management
    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        Integer userId = authorizationService.getCurrentUserId();
        UserResponse userResponse = userService.updateProfile(userId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully", userResponse));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile() {
        Integer userId = authorizationService.getCurrentUserId();
        UserResponse userResponse = userService.getUserById(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile retrieved successfully", userResponse));
    }

    @PutMapping("/{userId}/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @PathVariable Integer userId,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        UserResponse userResponse = userService.updateProfile(userId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully", userResponse));
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> changeMyPassword(@Valid @RequestBody PasswordChangeRequest request) {
        Integer userId = authorizationService.getCurrentUserId();
        boolean success = userService.changePassword(userId, request);
        if (success) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", "Password updated"));
        }
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "Failed to change password", null));
    }

    @PostMapping("/{userId}/change-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @PathVariable Integer userId,
            @Valid @RequestBody PasswordChangeRequest request) {
        boolean success = userService.changePassword(userId, request);
        if (success) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", "Password updated"));
        }
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "Failed to change password", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestParam String email) {
        boolean success = userService.resetPassword(email);
        if (success) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Password reset email sent", "Reset link sent"));
        }
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "Failed to send reset email", null));
    }

    // User Details
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Integer userId) {
        UserResponse userResponse = userService.getUserById(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User details retrieved successfully", userResponse));
    }

    // Security Operations
    @PostMapping("/{userId}/verify-security")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> verifySecurityAnswers(
            @PathVariable Integer userId,
            @RequestBody List<String> answers) {
        boolean verified = userService.verifySecurityAnswers(userId, answers);
        if (verified) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Security answers verified", "Verification successful"));
        }
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "Security verification failed", null));
    }

    // Admin Operations
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(Pageable pageable) {
        Page<UserResponse> users = userService.findAllUsers(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsersByStatus(
            @PathVariable User.Status status,
            Pageable pageable) {
        Page<UserResponse> users = userService.findByStatusPaginated(status, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users by status retrieved successfully", users));
    }

    @GetMapping("/pending-approvals")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getPendingApprovals() {
        List<UserResponse> users = userService.findPendingApprovals();
        return ResponseEntity.ok(new ApiResponse<>(true, "Pending approvals retrieved successfully", users));
    }

    @PostMapping("/{userId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> approveUser(
            @PathVariable Integer userId,
            @RequestParam Integer adminId) {
        UserResponse userResponse = userService.approveUser(userId, adminId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User approved successfully", userResponse));
    }

    @PostMapping("/{userId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> rejectUser(
            @PathVariable Integer userId,
            @RequestParam Integer adminId,
            @RequestParam String reason) {
        UserResponse userResponse = userService.rejectUser(userId, adminId, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "User rejected successfully", userResponse));
    }

    @PostMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(@PathVariable Integer userId) {
        UserResponse userResponse = userService.activateUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User activated successfully", userResponse));
    }

    @PostMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> deactivateUser(@PathVariable Integer userId) {
        UserResponse userResponse = userService.deactivateUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deactivated successfully", userResponse));
    }

    @PostMapping("/{userId}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> lockAccount(@PathVariable Integer userId) {
        userService.lockAccount(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account locked successfully", "Account locked"));
    }

    @PostMapping("/{userId}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> unlockAccount(@PathVariable Integer userId) {
        userService.unlockAccount(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account unlocked successfully", "Account unlocked"));
    }

    // Statistics
    @GetMapping("/stats/total")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getTotalUserCount() {
        long count = userService.getTotalUserCount();
        return ResponseEntity.ok(new ApiResponse<>(true, "Total user count retrieved", count));
    }

    @GetMapping("/stats/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getActiveUserCount() {
        long count = userService.getActiveUserCount();
        return ResponseEntity.ok(new ApiResponse<>(true, "Active user count retrieved", count));
    }

    @GetMapping("/stats/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getPendingUserCount() {
        long count = userService.getPendingUserCount();
        return ResponseEntity.ok(new ApiResponse<>(true, "Pending user count retrieved", count));
    }
}
