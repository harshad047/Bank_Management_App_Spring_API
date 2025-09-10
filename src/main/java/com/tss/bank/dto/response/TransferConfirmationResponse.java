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
public class TransferConfirmationResponse {
    
    private Integer transferId;
    private String transactionReference;
    private Integer fromAccountId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private String beneficiaryName;
    private BigDecimal amount;
    private BigDecimal charges;
    private BigDecimal totalAmount;
    private Date transferTime;
    private String status;
    private String message;
}
