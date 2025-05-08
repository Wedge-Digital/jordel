package com.auth.domain.validators;


import com.auth.services.Result;

public interface AbstractValidator<T> {
    Result<T> check(String value);
}
