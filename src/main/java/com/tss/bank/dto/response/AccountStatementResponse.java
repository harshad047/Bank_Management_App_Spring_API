package com.tss.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountStatementResponse {
    
    private Integer accountId;
    private String accountNumber;
    private String accountHolderName;
    private Date fromDate;
    private Date toDate;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private List<TransactionResponse> transactions;
    private String statementFormat;
    private Date generatedAt;
}
