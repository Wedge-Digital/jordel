package com.shared;

import com.shared.validators.ULIDConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class EntityID extends ValueObject {

    @NotNull(message = "cannot be null")
    @NotEmpty(message = "cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9]{26}$", message = "must be alphanumeric and 26 characters")
    @ULIDConstraint(message = "is not a valid ULID")
    protected String value;

    public EntityID(String id) {
        this.value = id;
    }

    public String toString(){
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
