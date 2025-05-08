package com.auth.domain;

import com.auth.domain.objects.Checker;
import com.auth.domain.validators.AbstractValidator;
import com.auth.domain.validators.CannotBeEmpty;
import com.auth.domain.validators.CannotBeNull;
import com.auth.domain.validators.ShallMatchRegExp;
import com.auth.services.Result;

public class CoachName {
    private final String value;

    private CoachName(String value) {
        this.value = value;
    }

    static Checker checker = new Checker(new AbstractValidator[] {
            new CannotBeNull("CoachName cannot be null"),
            new CannotBeEmpty("CoachName cannot be empty"),
            new ShallMatchRegExp("^[a-zA-Z0-9' _-]{1,50}$", "CoachName must be alphanumeric and 1-50 characters"),
    });

    public static Result<CoachName> fromString(String email) {
        Result<String> checkResult = checker.check(email);
        if (checkResult.isFailure()) {
            return Result.failure(checkResult.getError());
        }
        return Result.success(new CoachName(email));
    }

    @Override
    public String toString() {
        return value;
    }
}

