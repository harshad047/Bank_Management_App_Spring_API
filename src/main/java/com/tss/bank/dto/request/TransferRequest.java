package com.tss.bank.dto.request;

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
public class TransferRequest {
    
    @NotNull(message = "From account ID is required")
    private Integer fromAccountId;
    
    @NotBlank(message = "To account number is required")
    private String toAccountNumber;
    
    @NotBlank(message = "IFSC code is required")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code format")
    private String ifscCode;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Transfer amount must be at least 1.00")
    private BigDecimal amount;
    
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
    
    @NotBlank(message = "Beneficiary name is required")
    @Size(min = 2, max = 100, message = "Beneficiary name must be between 2 and 100 characters")
    private String beneficiaryName;
}
