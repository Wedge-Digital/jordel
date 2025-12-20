package com.bloodbowlclub.team_building.domain.roster;

import com.bloodbowlclub.lib.domain.ValueObject;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

import static com.bloodbowlclub.shared.constants.MAX_PLAYER_COUNT;

@JsonSerialize(using = ValueObjectSerializer.class)
public class PlayerMaxQuantity extends ValueObject {

    @Positive
    @Max(value = MAX_PLAYER_COUNT, message = "{player_qty.max}")
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
