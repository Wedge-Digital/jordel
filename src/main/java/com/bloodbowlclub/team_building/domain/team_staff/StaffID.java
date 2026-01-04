package com.bloodbowlclub.team_building.domain.team_staff;

import com.bloodbowlclub.lib.domain.ValueObject;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

/**
 * Value Object représentant l'identifiant d'un staff.
 * Format: Chaînes de caractères en majuscules avec underscores
 * Exemples: "APOTHECARY", "CHEERLEADERS", "COACH_ASSISTANTS"
 */
@JsonSerialize(using = ValueObjectSerializer.class)
public class StaffID extends ValueObject<String> {

    @NotEmpty(message = "{staffID.notEmpty}")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "{staffID.invalid}")
    private final String value;

    public StaffID(String id) {
        this.value = id;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equalsString(String other) {
        return this.value.equals(other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StaffID staffID)) return false;
        return value.equals(staffID.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
