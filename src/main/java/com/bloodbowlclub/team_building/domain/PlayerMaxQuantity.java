package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.domain.ValueObject;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@JsonSerialize(using = ValueObjectSerializer.class)
public class PlayerMaxQuantity extends ValueObject {

    @NotEmpty
    @Size(min = 0, max = 20, message = "must be between 3 and 100 characters")
    private final int value;

    public PlayerMaxQuantity(int value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equalsString(String other) {
        return String.valueOf(this.value).equals(other);
    }

    public int getValue() {
        return this.value;
    }
}
