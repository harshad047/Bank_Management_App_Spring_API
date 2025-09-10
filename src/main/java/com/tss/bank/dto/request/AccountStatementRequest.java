package com.tss.bank.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountStatementRequest {
    
    @NotNull(message = "Account ID is required")
    private Integer accountId;
    
    @NotNull(message = "From date is required")
    @Past(message = "From date must be in the past")
    private Date fromDate;
    
    @NotNull(message = "To date is required")
    @Past(message = "To date must be in the past")
    private Date toDate;
    
    private String format = "PDF"; // PDF, EXCEL, CSV
}
