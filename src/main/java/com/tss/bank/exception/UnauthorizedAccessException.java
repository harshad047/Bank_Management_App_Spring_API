package com.tss.bank.exception;

public class UnauthorizedAccessException extends BankApiException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedAccessException(String resource, Integer userId) {
        super("Unauthorized access to " + resource + " for user ID: " + userId);
    }
}
