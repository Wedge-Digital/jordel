package com.bloodbowlclub.lib.domain;

import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.validators.DomainValidator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;

public interface Validable {
    @JsonIgnore
    default boolean isNotValid(){
        return !validationErrors().isSuccess();
    }

    @JsonIgnore
    default boolean isValid(){
        return !isNotValid();
    }

    default ResultMap<Void> validationErrors(){
        HashMap<String, String> errorMap = (HashMap<String, String>) DomainValidator.getErrors(this);
        if(errorMap.isEmpty()){
            return ResultMap.success(null);
        }
        return ResultMap.failure(errorMap, ErrorCode.UNPROCESSABLE_ENTITY);
    }
}
