package com.bloodbowlclub.lib.services.result.exceptions;


import java.util.Map;

public class ResultException extends RuntimeException{
    private final Map<String, String> errors;

    public ResultException(Map<String, String> errors) {
        super("Business validation failed");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
