package com.tss.bank.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tss.bank.error.ResponseError;

@ControllerAdvice
public class GlobalExceptionHandler {

    // All API exceptions
    @ExceptionHandler({
        UserApiException.class,
        AccountApiException.class,
        TransactionApiException.class,
        AdminApiException.class,
        BeneficiaryApiException.class,
        FixedDepositApiException.class,
        TransferApiException.class,
        SecurityQuestionApiException.class,
        UserEnquiryApiException.class,
        FDApplicationApiException.class
    })
    public ResponseEntity<ResponseError> handleApiExceptions(BankApiException ex) {
        ResponseError error = new ResponseError(
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Generic business exception (fallback for BankApiException)
    @ExceptionHandler(BankApiException.class)
    public ResponseEntity<ResponseError> handleBankApiException(BankApiException ex) {
        ResponseError error = new ResponseError(
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Database constraint violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseError> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Data integrity violation: ";
        if (ex.getMessage().contains("Duplicate entry")) {
            message += "Record already exists with the provided data";
        } else if (ex.getMessage().contains("foreign key constraint")) {
            message += "Referenced record does not exist";
        } else {
            message += "Invalid data provided";
        }
        
        ResponseError error = new ResponseError(
                HttpStatus.CONFLICT.value(),
                System.currentTimeMillis(),
                message,
                null
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseError> handleValidations(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(err -> {
            errors.put(err.getField(), err.getDefaultMessage());
        });

        ResponseError responseError = new ResponseError(
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis(),
                "Validation failed",
                errors
        );

        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }

    // Fallback for all unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> handleGenericException(Exception ex) {
        ResponseError error = new ResponseError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                System.currentTimeMillis(),
                "An unexpected error occurred: " + ex.getMessage(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
