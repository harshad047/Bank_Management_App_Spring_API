package com.tss.bank.dto.response;

import com.tss.bank.entity.UserEnquiry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEnquiryResponse {
    
    private Integer enquiryId;
    private Integer userId;
    private UserEnquiry.QueryType queryType;
    private String description;
    private Date submittedAt;
    private Date resolvedAt;
    private String adminResponse;
    private UserEnquiry.Status status;
    private Date createdAt;
    private Date updatedAt;
}
