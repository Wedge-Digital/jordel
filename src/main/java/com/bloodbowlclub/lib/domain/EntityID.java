package com.bloodbowlclub.lib.domain;

import com.bloodbowlclub.lib.validators.ULIDConstraint;
import jakarta.validation.constraints.NotEmpty;

public class EntityID extends ValueObject<String> {

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.value.equals(o.toString());
    }

    @Override
    public boolean equalsString(String id){
        return this.value.equals(id);
    }


}
