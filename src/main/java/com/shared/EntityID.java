package com.shared;

import com.shared.validators.ULIDConstraint;
import jakarta.validation.constraints.NotEmpty;

public class EntityID extends ValueObject {

    @NotEmpty(message = "cannot be empty")
    @ULIDConstraint(message = "{ULID.invalid}")
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
