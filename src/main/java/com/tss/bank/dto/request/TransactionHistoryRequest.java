package com.tss.bank.dto.request;

import com.tss.bank.entity.Transaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;
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
public class TransactionHistoryRequest {
    
    @NotNull(message = "Account ID is required")
    private Integer accountId;
    
    @Past(message = "From date must be in the past")
    private Date fromDate;
    
    @Past(message = "To date must be in the past")
    private Date toDate;
    
    private Transaction.TxnType txnType;
    
    private Transaction.Channel channel;
    
    @PositiveOrZero(message = "Minimum amount must be zero or positive")
    private BigDecimal minAmount;
    
    @PositiveOrZero(message = "Maximum amount must be zero or positive")
    private BigDecimal maxAmount;
    
    @PositiveOrZero(message = "Page number must be zero or positive")
    private Integer page = 0;
    
    @PositiveOrZero(message = "Page size must be zero or positive")
    private Integer size = 10;
}
