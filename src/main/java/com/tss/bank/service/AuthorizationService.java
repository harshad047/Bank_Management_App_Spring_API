package com.tss.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tss.bank.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthorizationService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest request;

    /**
     * Get the current authenticated user's ID from JWT token
     */
    public Integer getCurrentUserId() {
        String token = extractTokenFromRequest();
        if (token != null) {
            return jwtUtil.extractUserId(token);
        }
        return null;
    }

    /**
     * Get the current authenticated user's username
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * Get the current authenticated user's role
     */
    public String getCurrentUserRole() {
        String token = extractTokenFromRequest();
        if (token != null) {
            return jwtUtil.extractRole(token);
        }
        return null;
    }

    /**
     * Check if the current user is an admin
     */
    public boolean isCurrentUserAdmin() {
        String role = getCurrentUserRole();
        return "ADMIN".equals(role) || "SUPER_ADMIN".equals(role);
    }

    /**
     * Check if the current user can access the specified user's data
     */
    public boolean canAccessUserData(Integer targetUserId) {
        if (isCurrentUserAdmin()) {
            return true; // Admins can access any user's data
        }
        
        Integer currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(targetUserId);
    }

    /**
     * Validate that the current user can only access their own data
     */
    public void validateUserAccess(Integer targetUserId) {
        if (!canAccessUserData(targetUserId)) {
            throw new SecurityException("Access denied: You can only access your own data");
        }
    }

    /**
     * Check if the current user can access the specified account
     */
    public boolean canAccessAccount(Integer accountId) {
        if (isCurrentUserAdmin()) {
            return true; // Admins can access any account
        }
        
        Integer currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }
        
        // For now, we'll assume account access is valid for authenticated users
        // In a real implementation, you would check account ownership in the database
        return true; // TODO: Implement actual account ownership check with database query
    }

    /**
     * Validate that the current user can access the specified account
     */
    public void validateAccountAccess(Integer accountId) {
        if (!canAccessAccount(accountId)) {
            throw new SecurityException("Access denied: You can only access your own accounts");
        }
    }

    /**
     * Extract JWT token from the Authorization header
     */
    private String extractTokenFromRequest() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
