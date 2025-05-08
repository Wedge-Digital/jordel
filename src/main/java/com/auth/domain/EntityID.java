package com.auth.domain;

import com.auth.domain.objects.Checker;
import com.auth.domain.validators.*;
import com.auth.services.Result;

public class EntityID {

    private final String id;

    static Checker checker = new Checker(new AbstractValidator[] {
            new CannotBeNull("EntityID cannot be null"),
            new CannotBeEmpty("EntityID cannot be empty"),
            new ShallMatchRegExp("^[a-zA-Z0-9]{26}$", "EntityID must be alphanumeric and 26 characters"),
            new ShallBeAValidUlid("EntityID must be a valid ULID")
    });

    private EntityID(String id) {
        this.id = id;
    }

    public static Result<EntityID> fromString(String id) {
        Result<String> checkResult = checker.check(id);
        if (checkResult.isFailure()) {
            return Result.failure(checkResult.getError());
        }
        return Result.success(new EntityID(id));
    }


}
