package com.bloodbowlclub.team_building.domain.team_stuff;

import com.bloodbowlclub.lib.domain.ValueObject;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

@JsonSerialize(using = ValueObjectSerializer.class)
public class StuffPrice extends ValueObject<Integer> {

    @Positive
    @Max(value = 500, message = "{player_price.max}")
    private final int value;

    public StuffPrice(int value) {
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
        return value;
    }
}
