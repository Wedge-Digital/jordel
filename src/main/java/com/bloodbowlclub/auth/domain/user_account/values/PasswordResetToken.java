package com.bloodbowlclub.auth.domain.user_account.values;

import com.bloodbowlclub.lib.domain.ValueObject;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.bloodbowlclub.lib.validators.ULIDConstraint;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotEmpty;
import ulid4j.Ulid;

@JsonSerialize(using = ValueObjectSerializer.class)
public class PasswordResetToken extends ValueObject {

    @NotEmpty(message = "cannot be empty")
    @ULIDConstraint(message = "{ULID.invalid}")
    protected String value;

    public PasswordResetToken(String value) {
        this.value = value;
    }

    public static PasswordResetToken New() {
        return new PasswordResetToken(new Ulid().create());
    }

    public String toString(){
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PasswordResetToken other = (PasswordResetToken) obj;
        if (value.equals(other.toString())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equalsString(String id){
        return this.value.equals(id);
    }

}