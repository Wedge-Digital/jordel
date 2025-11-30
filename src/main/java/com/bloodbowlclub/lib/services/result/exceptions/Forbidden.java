package com.bloodbowlclub.lib.services.result.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class Forbidden extends ResultException{
    public Forbidden(Map<String, String> errors) {
        super(errors);
    }
}
