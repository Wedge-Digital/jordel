package com.auth.domain.validators;

import com.auth.services.Result;
import com.auth.services.errors.FormatValidationError;

import java.util.Objects;

public class CannotBeNull implements AbstractValidator<String> {
    private final String errorMessage;

    public CannotBeNull(String valueName) {
        this.errorMessage = valueName;
    }

    public Result<String> check(String value) {
        if (Objects.equals(value, null)) {
            return Result.failure(new FormatValidationError(errorMessage));
        }
        return Result.success(value);
    }
}
