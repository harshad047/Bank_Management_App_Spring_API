package com.tss.bank.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminApprovalRequest {
    
    @NotNull(message = "Application ID is required")
    private Integer applicationId;
    
    @NotNull(message = "Approval status is required")
    private Boolean approved;
    
    @Size(max = 255, message = "Rejection reason must not exceed 255 characters")
    private String rejectionReason;
}
