package com.tss.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tss.bank.security.JwtUtil;
import com.tss.bank.repository.AccountRepository;
import com.tss.bank.repository.FixedDepositRepository;
import com.tss.bank.entity.Account;
import com.tss.bank.entity.FixedDeposit;
import com.tss.bank.exception.SecurityApiException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class AuthorizationService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest request;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private FixedDepositRepository fixedDepositRepository;

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
        
        // Check account ownership in the database
        return accountRepository.existsByAccountIdAndUserUserId(accountId, currentUserId);
    }

    /**
     * Validate that the current user can access the specified account
     */
    public void validateAccountAccess(Integer accountId) {
        if (!canAccessAccount(accountId)) {
            throw new SecurityApiException("Access denied: You can only access your own accounts");
        }
    }

    /**
     * Check if the current user can access the specified Fixed Deposit
     */
    public boolean canAccessFixedDeposit(Integer fdId) {
        if (isCurrentUserAdmin()) {
            return true; // Admins can access any FD
        }
        
        Integer currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }
        
        // Check FD ownership in the database
        Optional<FixedDeposit> fdOptional = fixedDepositRepository.findById(fdId);
        if (fdOptional.isPresent()) {
            FixedDeposit fd = fdOptional.get();
            return fd.getUser().getUserId().equals(currentUserId);
        }
        return false;
    }

    /**
     * Validate that the current user can access the specified Fixed Deposit
     */
    public void validateFixedDepositAccess(Integer fdId) {
        if (!canAccessFixedDeposit(fdId)) {
            throw new SecurityApiException("Access denied: You can only access your own fixed deposits");
        }
    }

    /**
     * Validate account ownership for transactions (deposit/withdrawal)
     */
    public void validateAccountOwnershipForTransaction(Integer accountId) {
        Integer currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new SecurityApiException("Authentication required: No valid JWT token found");
        }

        if (!isCurrentUserAdmin() && !accountRepository.existsByAccountIdAndUserUserId(accountId, currentUserId)) {
            throw new SecurityApiException("Access denied: You can only perform transactions on your own accounts");
        }
    }

    /**
     * Validate account ownership for transfers (only FROM account - user can transfer to any valid account)
     */
    public void validateAccountOwnershipForTransfer(Integer fromAccountId, String toAccountNumber) {
        Integer currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new SecurityApiException("Authentication required: No valid JWT token found");
        }

        // Always validate that the user owns the source account
        if (!isCurrentUserAdmin() && !accountRepository.existsByAccountIdAndUserUserId(fromAccountId, currentUserId)) {
            throw new SecurityApiException("Access denied: You can only transfer from your own accounts");
        }

        // Additional validation: Check if destination account exists (can be any account in the bank)
        Optional<Account> toAccount = accountRepository.findByAccountNumber(toAccountNumber);
        if (!toAccount.isPresent()) {
            throw new SecurityApiException("Invalid destination account: Account not found");
        }
    }

    /**
     * Validate FD account ownership for FD operations
     */
    public void validateFDAccountOwnership(Integer accountId) {
        Integer currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new SecurityApiException("Authentication required: No valid JWT token found");
        }

        if (!isCurrentUserAdmin() && !accountRepository.existsByAccountIdAndUserUserId(accountId, currentUserId)) {
            throw new SecurityApiException("Access denied: You can only create/manage FDs for your own accounts");
        }
    }

    /**
     * Get account owner's user ID
     */
    public Integer getAccountOwnerId(Integer accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isPresent()) {
            return account.get().getUser().getUserId();
        }
        return null;
    }

    /**
     * Validate that the current user matches the expected user ID
     */
    public void validateCurrentUser(Integer expectedUserId) {
        Integer currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new SecurityApiException("Authentication required: No valid JWT token found");
        }

        if (!isCurrentUserAdmin() && !currentUserId.equals(expectedUserId)) {
            throw new SecurityApiException("Access denied: User ID mismatch");
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
