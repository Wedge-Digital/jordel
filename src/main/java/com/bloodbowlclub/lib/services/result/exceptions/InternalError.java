package com.bloodbowlclub.lib.services.result.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalError extends ResultException {
    public InternalError(Map<String, String> errors) {
        super(errors);
    }
}
