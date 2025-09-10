package com.tss.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tss.bank.dto.request.AdminLoginRequest;
import com.tss.bank.dto.response.AdminResponse;
import com.tss.bank.dto.response.ApiResponse;
import com.tss.bank.dto.response.UserResponse;
import com.tss.bank.entity.User;
import com.tss.bank.service.AdminService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Authentication
    @PostMapping("/authenticate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminResponse>> authenticateAdmin(@Valid @RequestBody AdminLoginRequest request) {
        AdminResponse adminResponse = adminService.authenticateAdmin(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin authenticated successfully", adminResponse));
    }

    @PostMapping("/validate-credentials")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validateAdminCredentials(
            @RequestParam String username,
            @RequestParam String password) {
        boolean isValid = adminService.validateAdminCredentials(username, password);
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin credentials validation completed", isValid));
    }

    // User Management
    @PostMapping("/users/{userId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> approveUser(
            @PathVariable Integer userId,
            @RequestParam Integer adminId) {
        UserResponse userResponse = adminService.approveUser(userId, adminId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User approved successfully", userResponse));
    }

    @PostMapping("/users/{userId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> rejectUser(
            @PathVariable Integer userId,
            @RequestParam Integer adminId,
            @RequestParam String reason) {
        UserResponse userResponse = adminService.rejectUser(userId, adminId, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "User rejected successfully", userResponse));
    }

    @PostMapping("/users/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(
            @PathVariable Integer userId,
            @RequestParam Integer adminId) {
        UserResponse userResponse = adminService.activateUser(userId, adminId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User activated successfully", userResponse));
    }

    @PostMapping("/users/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> deactivateUser(
            @PathVariable Integer userId,
            @RequestParam Integer adminId) {
        UserResponse userResponse = adminService.deactivateUser(userId, adminId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deactivated successfully", userResponse));
    }

    // User Queries
    @GetMapping("/users/pending-approvals")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getPendingApprovals() {
        List<UserResponse> users = adminService.getPendingApprovals();
        return ResponseEntity.ok(new ApiResponse<>(true, "Pending approvals retrieved successfully", users));
    }

    @GetMapping("/users/pending-approvals-paginated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getPendingApprovalsPaginated(Pageable pageable) {
        Page<UserResponse> users = adminService.getPendingApprovalsPaginated(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Paginated pending approvals retrieved successfully", users));
    }

    @GetMapping("/users/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByStatus(@PathVariable User.Status status) {
        List<UserResponse> users = adminService.getUsersByStatus(status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users by status retrieved successfully", users));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(Pageable pageable) {
        Page<UserResponse> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "All users retrieved successfully", users));
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserDetails(@PathVariable Integer userId) {
        UserResponse userResponse = adminService.getUserDetails(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User details retrieved successfully", userResponse));
    }

    @GetMapping("/users/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(@RequestParam String searchTerm) {
        List<UserResponse> users = adminService.searchUsers(searchTerm);
        return ResponseEntity.ok(new ApiResponse<>(true, "User search completed successfully", users));
    }

    // User Statistics
    @GetMapping("/stats/users/total")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getTotalUserCount() {
        long count = adminService.getTotalUserCount();
        return ResponseEntity.ok(new ApiResponse<>(true, "Total user count retrieved successfully", count));
    }

    @GetMapping("/stats/users/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getActiveUserCount() {
        long count = adminService.getActiveUserCount();
        return ResponseEntity.ok(new ApiResponse<>(true, "Active user count retrieved successfully", count));
    }

    @GetMapping("/stats/users/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getPendingUserCount() {
        long count = adminService.getPendingUserCount();
        return ResponseEntity.ok(new ApiResponse<>(true, "Pending user count retrieved successfully", count));
    }

    @GetMapping("/stats/users/count/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getUserCountByStatus(@PathVariable User.Status status) {
        long count = adminService.getUserCountByStatus(status);
        return ResponseEntity.ok(new ApiResponse<>(true, "User count by status retrieved successfully", count));
    }

    // User Account Management
    @PostMapping("/users/{userId}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> lockUserAccount(
            @PathVariable Integer userId,
            @RequestParam Integer adminId) {
        boolean success = adminService.lockUserAccount(userId, adminId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User account lock operation completed", success));
    }

    @PostMapping("/users/{userId}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> unlockUserAccount(
            @PathVariable Integer userId,
            @RequestParam Integer adminId) {
        boolean success = adminService.unlockUserAccount(userId, adminId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User account unlock operation completed", success));
    }

    @PostMapping("/users/{userId}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> resetUserPassword(
            @PathVariable Integer userId,
            @RequestParam Integer adminId,
            @RequestParam String newPassword) {
        boolean success = adminService.resetUserPassword(userId, adminId, newPassword);
        return ResponseEntity.ok(new ApiResponse<>(true, "User password reset operation completed", success));
    }
}
