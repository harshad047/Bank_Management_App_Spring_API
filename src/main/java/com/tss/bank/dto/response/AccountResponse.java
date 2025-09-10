package com.tss.bank.dto.response;

import com.tss.bank.entity.Account;
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
public class AccountResponse {
    
    private Integer accountId;
    private Integer userId;
    private String accountNumber;
    private Account.AccountType accountType;
    private BigDecimal balance;
    private Account.Status status;
    private Date createdAt;
}
