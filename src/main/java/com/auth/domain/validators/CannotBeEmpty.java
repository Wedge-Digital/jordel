package com.auth.domain.validators;

import com.auth.services.Result;
import com.auth.services.errors.FormatValidationError;

import java.util.Objects;

public class CannotBeEmpty implements AbstractValidator<String> {

    private final String errorMessage;

    public CannotBeEmpty(String valueName) {
        this.errorMessage = valueName;
    }

    public Result<String> check(String value) {
        if (Objects.equals(value, "")) {
            return Result.failure(new FormatValidationError(errorMessage));
        }
        return Result.success(value);
    }
}
