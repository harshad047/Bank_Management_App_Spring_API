package com.tss.bank.exception;

public class BankApiException extends RuntimeException {
    public BankApiException(String message) {
        super(message);
    }
}
