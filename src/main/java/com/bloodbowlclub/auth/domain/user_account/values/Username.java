package com.bloodbowlclub.auth.domain.user_account.values;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.bloodbowlclub.lib.domain.ValueObject;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import jakarta.validation.constraints.NotEmpty;

@JsonSerialize(using = ValueObjectSerializer.class)
public class Username extends ValueObject {

    @NotEmpty(message = "cannot be empty")
    protected String value;

    public Username(String value) {
        this.value = value;
    }

    public String toString(){
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public boolean equalsString(String id){
        return this.value.equals(id);
    }
}