package com.bloodbowlclub.team_building.domain.roster;

import com.bloodbowlclub.lib.domain.ValueObject;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

@JsonSerialize(using = ValueObjectSerializer.class)
public class RerollBasePrice extends ValueObject<Integer> {

    @Positive
    @Max(value = 100, message = "{base_reroll_price.max}")
    private final int value;

    public RerollBasePrice(int value) {
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
