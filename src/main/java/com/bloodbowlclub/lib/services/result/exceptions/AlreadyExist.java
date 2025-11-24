package com.bloodbowlclub.lib.services.result.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class AlreadyExist extends ResultException{
    public AlreadyExist(Map<String, String> errors) {
        super(errors);
    }
}
