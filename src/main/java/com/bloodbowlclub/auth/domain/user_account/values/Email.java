package com.bloodbowlclub.auth.domain.user_account.values;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.bloodbowlclub.lib.domain.ValueObject;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;

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
