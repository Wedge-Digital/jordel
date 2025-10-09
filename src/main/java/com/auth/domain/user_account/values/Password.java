package com.auth.domain.user_account.values;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.shared.ValueObject;
import com.shared.domain.serializers.ValueObjectSerializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@JsonSerialize(using = ValueObjectSerializer.class)
public class Password extends ValueObject {

    private final String value;

    public Password(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean matches(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        return encoder.matches(password, value);
    }

    @Override
    public boolean equalsString(String id){
        return this.value.equals(id);
    }
}

