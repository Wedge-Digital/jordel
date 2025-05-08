package com.auth.services.errors;

public class FormatValidationError extends Error {

    public FormatValidationError(String message) {
        super(ErrorType.FORMAT_VALIDATION_ERROR, message);
    }
}
