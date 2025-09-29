package com.tss.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class SecurityApiException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public SecurityApiException(String message) {
        super(message);
    }

    public SecurityApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
