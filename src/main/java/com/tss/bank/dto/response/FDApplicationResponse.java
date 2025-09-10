package com.tss.bank.dto.response;

import com.tss.bank.entity.FDApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FDApplicationResponse {
    
    private Integer fdAppId;
    private Integer userId;
    private BigDecimal amount;
    private Integer tenureMonths;
    private BigDecimal interestRate;
    private Date applicationDate;
    private FDApplication.Status status;
    private String rejectionReason;
    private Integer approvedBy;
    private Date approvedAt;
}
