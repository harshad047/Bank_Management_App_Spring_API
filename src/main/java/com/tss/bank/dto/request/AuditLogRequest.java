package com.tss.bank.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogRequest {
    
    private Integer userId;
    
    private String action;
    
    private String entityType;
    
    private Integer entityId;
    
    @Past(message = "From date must be in the past")
    private Date fromDate;
    
    @Past(message = "To date must be in the past")
    private Date toDate;
    
    @PositiveOrZero(message = "Page number must be zero or positive")
    private Integer page = 0;
    
    @PositiveOrZero(message = "Page size must be zero or positive")
    private Integer size = 20;
}
