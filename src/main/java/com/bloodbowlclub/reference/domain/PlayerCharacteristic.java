package com.bloodbowlclub.reference.domain;

import com.bloodbowlclub.lib.domain.ValueObject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Value Object représentant une caractéristique de joueur (MA, ST, AG, PA, AV).
 * Isolé de team_building.domain.
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class PlayerCharacteristic extends ValueObject {
    @Min(value = 1, message = "Characteristic value must be at least 1")
    @Max(value = 10, message = "Characteristic value must be at most 10")
    private final int value;

    public PlayerCharacteristic(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equalsString(String id) {
        return id.equals(value);
    }
}