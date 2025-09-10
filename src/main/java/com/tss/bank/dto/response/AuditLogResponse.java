package com.tss.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {
    
    private Integer logId;
    private Integer userId;
    private String username;
    private String action;
    private String entityType;
    private Integer entityId;
    private String oldValues;
    private String newValues;
    private String ipAddress;
    private String userAgent;
    private Date timestamp;
    private String status; // SUCCESS, FAILED
    private String errorMessage;
}
