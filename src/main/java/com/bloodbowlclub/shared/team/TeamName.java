package com.bloodbowlclub.shared.team;

import com.bloodbowlclub.lib.domain.ValueObject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class TeamName extends ValueObject {

    @NotEmpty
    @Size(min = 3, max = 100, message = "must be between 3 and 100 characters")
    private final String value;

    public TeamName(String value) {
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

