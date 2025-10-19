package com.auth.domain.user_account.values;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lib.domain.ValueObject;
import com.lib.domain.serializers.ValueObjectSerializer;

@JsonSerialize(using = ValueObjectSerializer.class)
public class Email extends ValueObject {

    @jakarta.validation.constraints.Email(message = "{email.invalid}")
    private final String value;

    public Email(String email) {
        this.value = email;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equalsString(String id){
        return this.value.equals(id);
    }
}
