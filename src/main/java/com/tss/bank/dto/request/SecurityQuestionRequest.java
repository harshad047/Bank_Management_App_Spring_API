package com.tss.bank.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityQuestionRequest {
    
    @NotBlank(message = "Question text is required")
    @Size(min = 10, max = 255, message = "Question text must be between 10 and 255 characters")
    private String questionText;
}
