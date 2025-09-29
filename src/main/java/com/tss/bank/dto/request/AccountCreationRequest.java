package com.tss.bank.dto.request;

import com.tss.bank.entity.Account;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreationRequest {
    
    // userId will be extracted from JWT token, not from request body
    
    @NotNull(message = "Account type is required")
    private Account.AccountType accountType;
    
    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "500.0", message = "Initial balance must be at least 500.0")
    private BigDecimal initialBalance;
    
    @NotBlank(message = "Branch code is required")
    @Pattern(regexp = "^[A-Z0-9]{3,10}$", message = "Branch code must be 3-10 alphanumeric characters")
    private String branchCode;
}
