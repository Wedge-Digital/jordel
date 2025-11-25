package com.bloodbowlclub.auth.domain.user_account.values;

import com.bloodbowlclub.lib.services.result.Result;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.bloodbowlclub.lib.domain.ValueObject;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@JsonSerialize(using = ValueObjectSerializer.class)
public class Password extends ValueObject {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    private final String value;

    public Password(String value) {
        this.value = encoder.encode(value);;
    }

    @Override
    public String toString() {
        return value;
    }

    public Result<Void> matches(String password) {
        if (encoder.matches(password, value)) {
            return Result.success(null);
        }
        return Result.failure("Password does not match");
    }

    @Override
    public boolean equalsString(String id){
        return this.value.equals(id);
    }
}

