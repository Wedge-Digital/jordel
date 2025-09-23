package com.auth.services.errors;

public class NotFoundError extends Error {

    public NotFoundError(String context) {
        super(context);
    }

    public String getMessage() {
        return "Not found Error: " + getContext();
    }
}
