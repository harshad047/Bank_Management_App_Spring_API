package com.tss.bank.error;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseError {
    private int status;
    private long timestamp;
    private String message;              // main error message
    private Map<String, String> errors;  // detailed field-level validation errors
}
