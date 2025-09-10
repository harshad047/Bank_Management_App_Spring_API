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
public class TransferResponse {
    
    private Integer transferId;
    private Integer fromAccountId;
    private Integer toAccountId;
    private BigDecimal amount;
    private String description;
    private Date transferTime;
}
