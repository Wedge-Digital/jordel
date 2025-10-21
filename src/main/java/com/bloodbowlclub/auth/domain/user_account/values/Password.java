package com.bloodbowlclub.auth.domain.user_account.values;

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

    public boolean matches(String password) {
        return encoder.matches(password, value);
    }

    @Override
    public boolean equalsString(String id){
        return this.value.equals(id);
    }
}

