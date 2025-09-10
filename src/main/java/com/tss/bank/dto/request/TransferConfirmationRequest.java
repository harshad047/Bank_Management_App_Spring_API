package com.tss.bank.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferConfirmationRequest {
    
    @NotNull(message = "Transfer ID is required")
    private Integer transferId;
    
    @NotBlank(message = "OTP is required")
    @Size(min = 4, max = 6, message = "OTP must be between 4 and 6 digits")
    private String otp;
    
    @NotBlank(message = "Transaction password is required")
    private String transactionPassword;
}
