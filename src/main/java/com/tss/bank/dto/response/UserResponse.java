package com.tss.bank.dto.response;

import com.tss.bank.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    
    private Integer userId;
    private String username;
    private String email;
    private String phone;
    private User.Status status;
    private Integer approvedBy;
    private Date approvedAt;
    private String rejectionReason;
    private Date createdAt;
}
