package com.auth.services.errors;

public enum ErrorType {
    FORMAT_VALIDATION_ERROR,
    INVARIANT_VALIDATION_ERROR,
    NOT_FOUND_ERROR;

    public boolean isValidationError() {
        return this == FORMAT_VALIDATION_ERROR;
    }

    public boolean isInvariantValidationError() {
        return this == INVARIANT_VALIDATION_ERROR;
    }

    public boolean isNotFoundError() {
        return this == NOT_FOUND_ERROR;
    }
}