package com.auth.domain;

import com.auth.domain.objects.Checker;
import com.auth.domain.validators.*;
import com.auth.services.Result;

public class EmailAddress {
    private final String email;

    private EmailAddress(String email) {
        this.email = email;
    }

    static Checker checker = new Checker(new AbstractValidator[] {
            new CannotBeNull("Email address cannot be null"),
            new CannotBeEmpty("Email address cannot be empty"),
            new ShallMatchRegExp("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", "Bad format for email address"),
    });

    public static Result<EmailAddress> fromString(String email) {
        Result<String> checkResult = checker.check(email);
        if (checkResult.isFailure()) {
            return Result.failure(checkResult.getError());
        }
        return Result.success(new EmailAddress(email));
    }

    @Override
    public String toString() {
        return email;
    }
}
