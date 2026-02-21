package com.bloodbowlclub.shared.inducement;

import com.bloodbowlclub.lib.domain.ValueObject;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

/**
 * Value Object représentant l'identifiant d'un roster.
 * Format: Chaînes de caractères en majuscules avec chiffres, underscores et espaces
 * Exemples: "HUMAN", "ORC", "CHAOS_CHOSEN", "OLD_WORLD_ALLIANCE"
 */
@JsonSerialize(using = ValueObjectSerializer.class)
public class InducementID extends ValueObject<String> {

    @NotEmpty(message = "{rosterID.notEmpty}")
    @Pattern(regexp = "^[A-Z][A-Z0-9_ ]*$", message = "{rosterID.invalid}")
    private final String value;

    public InducementID(String id) {
        this.value = id;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equalsString(String id) {
        return this.value.equals(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InducementID rosterID = (InducementID) o;
        return value.equals(rosterID.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
