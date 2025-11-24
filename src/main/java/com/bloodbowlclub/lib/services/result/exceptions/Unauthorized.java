package com.bloodbowlclub.lib.services.result.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class Unauthorized extends ResultException{
    public Unauthorized(Map<String, String> errors) {
        super(errors);
    }
}
