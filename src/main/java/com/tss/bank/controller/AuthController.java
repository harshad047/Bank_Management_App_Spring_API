package com.tss.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.tss.bank.dto.request.UserLoginRequest;
import com.tss.bank.dto.request.UserRegistrationRequest;
import com.tss.bank.dto.response.ApiResponse;
import com.tss.bank.dto.response.UserResponse;
import com.tss.bank.entity.User;
import com.tss.bank.entity.Admin;

import java.util.Optional;
import com.tss.bank.security.JwtUtil;
import com.tss.bank.service.UserService;
import com.tss.bank.service.AdminService;

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

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse userResponse = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User registered successfully", userResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody UserLoginRequest request) {
        try {
            // First try to authenticate as user
            boolean isUserValid = userService.validateCredentials(request.getUsername(), request.getPassword());
            
            if (isUserValid) {
                Optional<User> userOptional = userService.findByUsername(request.getUsername());
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    String role = user.getRole() != null ? user.getRole().toString() : "USER";
                    
                    String token = jwtUtil.generateToken(request.getUsername(), role);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("token", token);
                    response.put("username", user.getUsername());
                    response.put("role", role);
                    response.put("userId", user.getUserId());
                    response.put("userType", "USER");
                    
                    return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
                }
            }
            
            // If user authentication fails, try admin authentication
            boolean isAdminValid = adminService.validateAdminCredentials(request.getUsername(), request.getPassword());
            
            if (isAdminValid) {
                Optional<Admin> adminOptional = adminService.findByUsername(request.getUsername());
                if (adminOptional.isPresent()) {
                    Admin admin = adminOptional.get();
                    String role = admin.getIsSuperAdmin() != null && admin.getIsSuperAdmin() ? "SUPER_ADMIN" : "ADMIN";
                    
                    String token = jwtUtil.generateToken(request.getUsername(), role);
                    
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
                    .body(new ApiResponse<>(false, "Invalid credentials", null));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Authentication failed: " + e.getMessage(), null));
        }
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
