package com.bloodbowlclub.team_building.domain.ruleset;

import com.bloodbowlclub.lib.domain.ValueObject;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@JsonSerialize(using = ValueObjectSerializer.class)
public class RulesetName extends ValueObject<String> {

    @NotEmpty
    @Size(min = 3, max = 100, message = "must be between 3 and 100 characters")
    private final String value;

    public RulesetName(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equalsString(String other) {
        return this.value.equals(other);
    }
}
