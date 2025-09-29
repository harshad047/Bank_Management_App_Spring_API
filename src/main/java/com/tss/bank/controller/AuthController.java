package com.tss.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.tss.bank.dto.request.UserLoginRequest;
import com.tss.bank.dto.request.UserRegistrationRequest;
import com.tss.bank.dto.request.LoginWithOTPRequest;
import com.tss.bank.dto.request.OTPVerificationRequest;
import com.tss.bank.dto.response.ApiResponse;
import com.tss.bank.dto.response.UserResponse;
import com.tss.bank.entity.User;
import com.tss.bank.entity.Admin;

import java.util.Optional;
import com.tss.bank.security.JwtUtil;
import com.tss.bank.service.UserService;
import com.tss.bank.service.AdminService;
import com.tss.bank.service.OTPService;
import com.tss.bank.service.EmailVerificationService;
import com.tss.bank.entity.OTP;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse userResponse = userService.registerUser(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("user", userResponse);
        response.put("message", "Registration successful. Please check your email for verification OTP.");
        response.put("nextStep", "verify-email");
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User registered successfully. Email verification required.", response));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyEmail(@Valid @RequestBody OTPVerificationRequest request) {
        try {
            boolean isVerified = emailVerificationService.verifyEmailWithOTP(request.getEmail(), request.getOtpCode());
            
            if (isVerified) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Email verified successfully. Your account is now pending admin approval.");
                response.put("status", "PENDING");
                
                return ResponseEntity.ok(new ApiResponse<>(true, "Email verification successful", response));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Invalid or expired OTP", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Email verification failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/resend-verification-otp")
    public ResponseEntity<ApiResponse<Map<String, Object>>> resendVerificationOTP(@RequestParam String email) {
        try {
            emailVerificationService.resendVerificationOTP(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Verification OTP sent to your email");
            response.put("email", maskEmail(email));
            
            return ResponseEntity.ok(new ApiResponse<>(true, "OTP sent successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Failed to send OTP: " + e.getMessage(), null));
        }
    }

    @PostMapping("/admin-login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> adminLogin(@Valid @RequestBody UserLoginRequest request) {
        try {
            // Admin login without OTP
            boolean isAdminValid = adminService.validateAdminCredentials(request.getUsername(), request.getPassword());
            
            if (isAdminValid) {
                Optional<Admin> adminOptional = adminService.findByUsername(request.getUsername());
                if (adminOptional.isPresent()) {
                    Admin admin = adminOptional.get();
                    String role = admin.getIsSuperAdmin() != null && admin.getIsSuperAdmin() ? "SUPER_ADMIN" : "ADMIN";
                    
                    String token = jwtUtil.generateToken(admin.getUsername(), role, admin.getAdminId());
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("token", token);
                    response.put("username", admin.getUsername());
                    response.put("role", role);
                    response.put("userId", admin.getAdminId());
                    response.put("userType", "ADMIN");
                    
                    return ResponseEntity.ok(new ApiResponse<>(true, "Admin login successful", response));
                }
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Invalid admin credentials", null));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Admin authentication failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/login-step1")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginStep1(@Valid @RequestBody UserLoginRequest request) {
        try {
            // Only for users - with OTP verification
            boolean isUserValid = userService.validateCredentials(request.getUsername(), request.getPassword());
            
            if (isUserValid) {
                Optional<User> userOptional = userService.findByUsername(request.getUsername());
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    
                    // Check user status before sending OTP
                    if (user.getStatus() != User.Status.ACTIVE) {
                        String errorMessage = "Account is not active. Current status: " + user.getStatus();
                        if (user.getStatus() == User.Status.PENDING) {
                            errorMessage = "Your account is pending for approval.";
                        } else if (user.getStatus() == User.Status.REJECTED) {
                            errorMessage = "Your account has been rejected.";
                        } else if (user.getStatus() == User.Status.INACTIVE) {
                            errorMessage = "Your account is inactive. Please contact support.";
                        }
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new ApiResponse<>(false, errorMessage, null));
                    }

                    // Generate and send OTP
                    otpService.generateAndSendOTP(user.getEmail(), OTP.OTPType.LOGIN);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "OTP sent to your registered email");
                    response.put("email", maskEmail(user.getEmail()));
                    response.put("nextStep", "verify-otp");
                    
                    return ResponseEntity.ok(new ApiResponse<>(true, "OTP sent successfully", response));
                }
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Invalid user credentials", null));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Authentication failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/login-step2")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginStep2(@Valid @RequestBody OTPVerificationRequest request) {
        try {
            // Verify OTP
            boolean isOTPValid = otpService.verifyOTP(request.getEmail(), request.getOtpCode(), OTP.OTPType.LOGIN);
            
            if (!isOTPValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Invalid or expired OTP", null));
            }

            // Find user by email and generate JWT token (only for users, not admins)
            Optional<User> userOptional = userService.findByEmail(request.getEmail());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String role = user.getRole() != null ? user.getRole().toString() : "USER";
                
                String token = jwtUtil.generateToken(user.getUsername(), role, user.getUserId());
                
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("username", user.getUsername());
                response.put("role", role);
                response.put("userId", user.getUserId());
                response.put("userType", "USER");
                
                return ResponseEntity.ok(new ApiResponse<>(true, "User login successful", response));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "User not found", null));
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "OTP verification failed: " + e.getMessage(), null));
        }
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 2) {
            return email;
        }
        
        String maskedUsername = username.charAt(0) + "*".repeat(username.length() - 2) + username.charAt(username.length() - 1);
        return maskedUsername + "@" + domain;
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshToken(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                String username = jwtUtil.extractUsername(jwtToken);
                String role = jwtUtil.extractRole(jwtToken);
                
                // Generate new token
                String newToken = jwtUtil.generateToken(username, role);
                
                Map<String, Object> response = new HashMap<>();
                response.put("token", newToken);
                response.put("username", username);
                response.put("role", role);
                
                return ResponseEntity.ok(new ApiResponse<>(true, "Token refreshed successfully", response));
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Invalid token format", null));
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Token refresh failed: " + e.getMessage(), null));
        }
    }
}
