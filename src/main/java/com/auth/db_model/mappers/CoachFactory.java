package com.auth.db_model.mappers;

import com.auth.db_model.User;
import com.auth.domain.*;
import com.auth.services.Result;
import com.auth.services.errors.Error;

import java.util.Vector;

public class CoachFactory {

    private static Vector<Error> collectErrors(Result<?>... results) {
        Vector<Error> errors = new Vector<Error>();
        for (Result<?> result : results) {
            if (result.isFailure()) {
                errors.add(result.getError());
            }
        }
        return errors;
    }

    public static Result<Coach> fromUserModel(User user) {
        Result<EntityID> entityIDResult = EntityID.fromString(user.getId());
        Result<CoachName> coachName = CoachName.fromString(user.getCoachName());
        Result<EmailAddress> emailAddress = EmailAddress.fromString(user.getEmail());
        Result<EncryptedPassword> encPwd = EncryptedPassword.fromString(user.getPassword());

        Vector<Error> errors = collectErrors(entityIDResult, coachName, emailAddress, encPwd);

        if (errors.isEmpty()) {
            Coach c = new Coach(
                    entityIDResult.getValue(),
                    coachName.getValue(),
                    emailAddress.getValue(),
                    encPwd.getValue(),
                    user.getIsActive(),
                    user.getCreatedAt(),
                    user.getLastLogin()
            );
            return Result.success(c);
        }
        return Result.failure(errors);
    }
}
