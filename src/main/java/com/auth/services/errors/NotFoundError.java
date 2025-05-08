package com.auth.services.errors;

public class NotFoundError extends Error {

    public NotFoundError(String message) {
        super(ErrorType.NOT_FOUND_ERROR, message);
    }
}
