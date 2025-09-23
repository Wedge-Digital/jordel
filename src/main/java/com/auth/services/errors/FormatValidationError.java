package com.auth.services.errors;

public class FormatValidationError extends Error {

    public FormatValidationError(String context) {
        super(context);
    }

    @Override
    public String getMessage() {
        return "Format Validation Error: " + getContext();
    }
}
