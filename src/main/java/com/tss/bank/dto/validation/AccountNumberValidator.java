package com.tss.bank.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AccountNumberValidator implements ConstraintValidator<ValidAccountNumber, String> {
    
    @Override
    public void initialize(ValidAccountNumber constraintAnnotation) {
        // Initialization logic if needed
    }
    
    @Override
    public boolean isValid(String accountNumber, ConstraintValidatorContext context) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return false;
        }
        
        // Remove any spaces or special characters
        String cleanAccountNumber = accountNumber.replaceAll("[^0-9]", "");
        
        // Check length (10-20 digits)
        if (cleanAccountNumber.length() < 10 || cleanAccountNumber.length() > 20) {
            return false;
        }
        
        // Check if all characters are digits
        return cleanAccountNumber.matches("^[0-9]+$");
    }
}
