package com.tss.bank.exception;

public class BranchApiException extends RuntimeException {
    public BranchApiException(String message) {
        super(message);
    }

    public BranchApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
