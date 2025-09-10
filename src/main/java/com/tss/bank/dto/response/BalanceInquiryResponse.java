package com.tss.bank.dto.response;

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
public class BalanceInquiryResponse {
    
    private Integer accountId;
    private String accountNumber;
    private BigDecimal availableBalance;
    private BigDecimal totalBalance;
    private Date lastTransactionDate;
    private Date inquiryTime;
}
