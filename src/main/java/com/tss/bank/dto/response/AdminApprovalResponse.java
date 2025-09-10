package com.tss.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminApprovalResponse {
    
    private Integer approvalId;
    private Integer userId;
    private Integer adminId;
    private String requestType;
    private String description;
    private String status;
    private String priority;
    private String comments;
    private String rejectionReason;
    private String referenceNumber;
    private LocalDateTime requestDate;
    private LocalDateTime processedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // User details
    private String userName;
    private String userEmail;
    
    // Admin details
    private String adminName;
    private String adminEmail;
}
