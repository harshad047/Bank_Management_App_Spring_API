package com.tss.bank.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tss.bank.dto.request.UserEnquiryRequest;
import com.tss.bank.dto.response.ApiResponse;
import com.tss.bank.dto.response.UserEnquiryResponse;
import com.tss.bank.entity.User;
import com.tss.bank.entity.UserEnquiry;
import com.tss.bank.exception.UserEnquiryApiException;
import com.tss.bank.service.MappingService;
import com.tss.bank.service.UserEnquiryService;
import com.tss.bank.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/enquiries")
public class UserEnquiryController {

    @Autowired
    private UserEnquiryService userEnquiryService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MappingService mappingService;

    /**
     * Create new enquiry (User only)
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserEnquiryResponse>> createEnquiry(
            @Valid @RequestBody UserEnquiryRequest request,
            Authentication authentication) {
        
        // Get current user
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserEnquiryApiException("User not found"));
        
        // Create enquiry
        UserEnquiry enquiry = UserEnquiry.builder()
                .user(user)
                .queryType(request.getQueryType())
                .description(request.getDescription())
                .status(UserEnquiry.Status.OPEN)
                .submittedAt(new Date())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
        
        UserEnquiry savedEnquiry = userEnquiryService.save(enquiry);
        UserEnquiryResponse response = mappingService.map(savedEnquiry, UserEnquiryResponse.class);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Enquiry submitted successfully", response));
    }

    /**
     * Get user's own enquiries
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<UserEnquiryResponse>>> getMyEnquiries(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserEnquiryApiException("User not found"));
        
        List<UserEnquiry> enquiries = userEnquiryService.findByUserId(user.getUserId());
        List<UserEnquiryResponse> responses = enquiries.stream()
                .map(enquiry -> mappingService.map(enquiry, UserEnquiryResponse.class))
                .toList();
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Enquiries retrieved successfully", responses));
    }

    /**
     * Get enquiry by ID (User can only see their own, Admin can see all)
     */
    @GetMapping("/{enquiryId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserEnquiryResponse>> getEnquiryById(
            @PathVariable Integer enquiryId,
            Authentication authentication) {
        
        UserEnquiry enquiry = userEnquiryService.findById(enquiryId)
                .orElseThrow(() -> new UserEnquiryApiException("Enquiry not found with ID: " + enquiryId));
        
        // Check if user can access this enquiry
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new UserEnquiryApiException("User not found"));
        
        // Users can only see their own enquiries, admins can see all
        if (currentUser.getRole() == User.Role.USER && 
            !enquiry.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UserEnquiryApiException("Access denied to this enquiry");
        }
        
        UserEnquiryResponse response = mappingService.map(enquiry, UserEnquiryResponse.class);
        return ResponseEntity.ok(new ApiResponse<>(true, "Enquiry retrieved successfully", response));
    }

    /**
     * Get all enquiries (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserEnquiryResponse>>> getAllEnquiries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        List<UserEnquiry> enquiries = userEnquiryService.findAll();
        List<UserEnquiryResponse> responses = enquiries.stream()
                .map(enquiry -> mappingService.map(enquiry, UserEnquiryResponse.class))
                .toList();
        
        return ResponseEntity.ok(new ApiResponse<>(true, "All enquiries retrieved successfully", responses));
    }

    /**
     * Get enquiries by status (Admin only)
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserEnquiryResponse>>> getEnquiriesByStatus(
            @PathVariable UserEnquiry.Status status) {
        
        List<UserEnquiry> enquiries = userEnquiryService.findByStatus(status);
        List<UserEnquiryResponse> responses = enquiries.stream()
                .map(enquiry -> mappingService.map(enquiry, UserEnquiryResponse.class))
                .toList();
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Enquiries by status retrieved successfully", responses));
    }

    /**
     * Get enquiries by query type (Admin only)
     */
    @GetMapping("/type/{queryType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserEnquiryResponse>>> getEnquiriesByType(
            @PathVariable UserEnquiry.QueryType queryType) {
        
        List<UserEnquiry> enquiries = userEnquiryService.findByQueryType(queryType);
        List<UserEnquiryResponse> responses = enquiries.stream()
                .map(enquiry -> mappingService.map(enquiry, UserEnquiryResponse.class))
                .toList();
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Enquiries by type retrieved successfully", responses));
    }

    /**
     * Respond to enquiry (Admin only)
     */
    @PutMapping("/{enquiryId}/respond")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserEnquiryResponse>> respondToEnquiry(
            @PathVariable Integer enquiryId,
            @RequestBody String adminResponse) {
        
        UserEnquiry enquiry = userEnquiryService.findById(enquiryId)
                .orElseThrow(() -> new UserEnquiryApiException("Enquiry not found with ID: " + enquiryId));
        
        enquiry.setAdminResponse(adminResponse);
        enquiry.setStatus(UserEnquiry.Status.CLOSED);
        enquiry.setResolvedAt(new Date());
        enquiry.setUpdatedAt(new Date());
        
        UserEnquiry updatedEnquiry = userEnquiryService.update(enquiry);
        UserEnquiryResponse response = mappingService.map(updatedEnquiry, UserEnquiryResponse.class);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Enquiry responded successfully", response));
    }

    /**
     * Close enquiry (Admin only)
     */
    @PutMapping("/{enquiryId}/close")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserEnquiryResponse>> closeEnquiry(@PathVariable Integer enquiryId) {
        
        UserEnquiry enquiry = userEnquiryService.findById(enquiryId)
                .orElseThrow(() -> new UserEnquiryApiException("Enquiry not found with ID: " + enquiryId));
        
        enquiry.setStatus(UserEnquiry.Status.CLOSED);
        enquiry.setResolvedAt(new Date());
        enquiry.setUpdatedAt(new Date());
        
        UserEnquiry updatedEnquiry = userEnquiryService.update(enquiry);
        UserEnquiryResponse response = mappingService.map(updatedEnquiry, UserEnquiryResponse.class);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Enquiry closed successfully", response));
    }

    /**
     * Get enquiry statistics (Admin only)
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getEnquiryStats() {
        
        long totalEnquiries = userEnquiryService.findAll().size();
        long openEnquiries = userEnquiryService.countOpenEnquiries();
        long closedEnquiries = totalEnquiries - openEnquiries;
        
        Object stats = new Object() {
            public final long total = totalEnquiries;
            public final long open = openEnquiries;
            public final long closed = closedEnquiries;
        };
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Enquiry statistics retrieved successfully", stats));
    }

    /**
     * Delete enquiry (Admin only)
     */
    @DeleteMapping("/{enquiryId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEnquiry(@PathVariable Integer enquiryId) {
        
        if (!userEnquiryService.findById(enquiryId).isPresent()) {
            throw new UserEnquiryApiException("Enquiry not found with ID: " + enquiryId);
        }
        
        userEnquiryService.deleteById(enquiryId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Enquiry deleted successfully", null));
    }
}
