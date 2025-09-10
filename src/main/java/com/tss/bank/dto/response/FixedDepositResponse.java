package com.tss.bank.dto.response;

import com.tss.bank.entity.FixedDeposit;
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
public class FixedDepositResponse {
    
    private Integer fdId;
    private Integer fdAppId;
    private Integer userId;
    private Integer accountId;
    private BigDecimal amount;
    private Integer tenureMonths;
    private BigDecimal interestRate;
    private BigDecimal maturityAmount;
    private Date startDate;
    private Date maturityDate;
    private FixedDeposit.Status status;
    private Date createdAt;
    private Date updatedAt;
}
