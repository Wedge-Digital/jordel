package com.bloodbowlclub.lib.services.result.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequest extends ResultException{
    public BadRequest(Map<String, String> errors) {
        super(errors);
    }
}
