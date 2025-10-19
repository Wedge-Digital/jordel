package com.shared.coach;

import com.lib.domain.ValueObject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class CoachName extends ValueObject {

    @NotEmpty
    @Size(min = 3, max = 50, message = "must be between 3 and 50 characters")
    private final String value;

    public CoachName(String value) {
        this.value = value;
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

