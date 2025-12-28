package com.bloodbowlclub.team_building.domain.team_staff;

import com.bloodbowlclub.lib.domain.ValueObject;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

@JsonSerialize(using = ValueObjectSerializer.class)
public class StaffMaxQuantity extends ValueObject {

    @Positive
    @Max(value = 6, message = "{stuff.max}")
    private final int value;

    public StaffMaxQuantity(int value) {
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
