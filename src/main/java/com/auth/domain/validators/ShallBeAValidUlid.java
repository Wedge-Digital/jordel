package com.auth.domain.validators;

import com.auth.services.Result;
import com.auth.services.errors.FormatValidationError;
import ulid4j.Ulid;

import java.util.Objects;

public class ShallBeAValidUlid implements AbstractValidator<String> {
    private final String errorMessage;

    public ShallBeAValidUlid(String valueName) {
        this.errorMessage = valueName;
    }

    public Result<String> check(String value) {
        if (!Ulid.isValid(value)) {
            return Result.failure(new FormatValidationError(errorMessage));
        }
        return Result.success(value);
    }
}
