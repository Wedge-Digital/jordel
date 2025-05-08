package com.auth.domain;

import com.auth.domain.objects.Checker;
import com.auth.domain.validators.AbstractValidator;
import com.auth.domain.validators.CannotBeEmpty;
import com.auth.domain.validators.CannotBeNull;
import com.auth.domain.validators.ShallMatchRegExp;
import com.auth.services.Result;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncryptedPassword {
    private final String value;

    private EncryptedPassword(String value) {
        this.value = value;
    }

    static Checker checker = new Checker(new AbstractValidator[] {
            new CannotBeNull("Encrypted Password cannot be null"),
            new CannotBeEmpty("Encrypted Password cannot be empty"),
            new ShallMatchRegExp("^\\$2[aby]?\\$\\d{1,2}\\$[./A-Za-z0-9]{53}$", "Encrypted password is not a valid encrypted string"),
    });

    public static Result<EncryptedPassword> fromString(String email) {
        Result<String> checkResult = checker.check(email);
        if (checkResult.isFailure()) {
            return Result.failure(checkResult.getError());
        }
        return Result.success(new EncryptedPassword(email));
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean matches(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        return encoder.matches(password, value);
    }
}

