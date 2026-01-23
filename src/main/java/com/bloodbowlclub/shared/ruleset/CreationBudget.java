package com.bloodbowlclub.shared.ruleset;

import com.bloodbowlclub.lib.domain.ValueObject;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
@JsonSerialize(using = ValueObjectSerializer.class)
public class CreationBudget extends ValueObject<Integer> {

    @Positive
    private final int value;

    public CreationBudget(int value) {
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

}
