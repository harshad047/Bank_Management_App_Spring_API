package com.tss.bank.dto.response;

import com.tss.bank.entity.Transaction;
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
public class TransactionResponse {
    
    private Integer txnId;
    private Integer userId;
    private Integer accountId;
    private Transaction.TxnType txnType;
    private BigDecimal amount;
    private String description;
    private Date txnTime;
    private BigDecimal balanceAfter;
    private Transaction.Channel channel;
    private Date createdAt;
}
