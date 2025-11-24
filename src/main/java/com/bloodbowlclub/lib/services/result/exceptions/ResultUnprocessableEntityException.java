package com.bloodbowlclub.lib.services.result.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ResultUnprocessableEntityException extends ResultException{
    public ResultUnprocessableEntityException(Map<String, String> errors) {
        super(errors);
    }
}
