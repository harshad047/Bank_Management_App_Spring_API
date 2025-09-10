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
public class NotificationResponse {
    
    private Integer notificationId;
    private Integer userId;
    private String title;
    private String message;
    private String type;
    private String priority;
    private Boolean isRead;
    private Date sentAt;
    private Date readAt;
    private String status; // SENT, DELIVERED, FAILED, READ
}
