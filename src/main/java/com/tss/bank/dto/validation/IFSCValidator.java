package com.tss.bank.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IFSCValidator implements ConstraintValidator<ValidIFSC, String> {
    
    @Override
    public void initialize(ValidIFSC constraintAnnotation) {
        // Initialization logic if needed
    }
    
    @Override
    public boolean isValid(String ifscCode, ConstraintValidatorContext context) {
        if (ifscCode == null || ifscCode.trim().isEmpty()) {
            return false;
        }
        
        // Remove any spaces and convert to uppercase
        String cleanIFSC = ifscCode.replaceAll("\\s", "").toUpperCase();
        
        // IFSC format: First 4 characters are alphabets, 5th character is 0, last 6 characters are alphanumeric
        return cleanIFSC.matches("^[A-Z]{4}0[A-Z0-9]{6}$");
    }
}
