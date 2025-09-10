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
public class UserSecurityAnswerRequest {
    
    @NotNull(message = "Question ID is required")
    private Integer questionId;
    
    @NotBlank(message = "Answer is required")
    @Size(min = 2, max = 255, message = "Answer must be between 2 and 255 characters")
    private String answer;
}
