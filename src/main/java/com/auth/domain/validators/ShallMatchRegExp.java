package com.auth.domain.validators;

import com.auth.services.Result;
import com.auth.services.errors.FormatValidationError;


public class ShallMatchRegExp implements AbstractValidator<String> {
    private final String errorMessage;
    private final String regexpToMatch;

    public ShallMatchRegExp(String RegexpToMatch, String valueName) {
        this.regexpToMatch = RegexpToMatch;
        this.errorMessage = valueName;
    }

    public Result<String> check(String value) {
        if (!value.matches(this.regexpToMatch)) {
            return Result.failure(new FormatValidationError(errorMessage));
        }
        return Result.success(value);
    }
}
