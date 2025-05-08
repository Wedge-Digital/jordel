package com.auth.domain.objects;

import com.auth.domain.validators.*;
import com.auth.services.Result;

public class Checker {

    private final AbstractValidator[] formatValidators;

    public Checker(AbstractValidator[] formatValidators) {
        // Constructor
        this.formatValidators = formatValidators;
    }

    public Result<String> check(String id) {
        for (AbstractValidator validator : this.formatValidators) {
            Result<String> result = validator.check(id);
            if (result.isFailure()) {
                return Result.failure(result.getError());
            }
        }
        return Result.success(id);
    }
}
