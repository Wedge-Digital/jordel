package com.auth.security;

public enum HeaderError {
    EMPTY_OR_NULL_HEADER("Empty Auth Header"),
    INVALID_HEADER("Invalid Auth Header");

    private final String message;

    HeaderError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}