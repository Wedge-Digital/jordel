package com.bloodbowlclub.team_building.domain.roster;

import com.bloodbowlclub.lib.domain.ValueObject;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

/**
 * Value Object représentant l'identifiant d'une définition de joueur.
 * Format: EQUIPE__POSITION avec majuscules, chiffres, underscores et espaces
 * Exemples: "HUMAN__LINEMAN", "CHAOS_CHOSEN__TROLL", "OLD_WORLD_ALLIANCE__DWARF_BLOCKER"
 */
@JsonSerialize(using = ValueObjectSerializer.class)
public class PlayerDefinitionID extends ValueObject<String> {

    @NotEmpty(message = "{playerDefinitionID.notEmpty}")
    @Pattern(
        regexp = "^[A-Z'][A-Z0-9_' -]*__[A-Z'][A-Z0-9_' -]*$",
        message = "{playerDefinitionID.invalid}"
    )
    private final String value;

    public PlayerDefinitionID(String id) {
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
        PlayerDefinitionID that = (PlayerDefinitionID) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
