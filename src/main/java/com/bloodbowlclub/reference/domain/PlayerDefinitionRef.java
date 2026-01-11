package com.bloodbowlclub.reference.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Représentation d'une définition de joueur dans les données de référence.
 * Immutable, chargée depuis JSON au démarrage.
 * ISOLÉE de team_building.domain.PlayerDefinition.
 */
@Getter
@Builder
public class PlayerDefinitionRef {

    @NotNull
    private final String uid;

    @NotNull
    private final String positionName;

    @Valid
    @NotNull
    private final Integer cost; // Prix en milliers de gold

    @Valid
    @NotNull
    private final Integer maxQuantity;

    // Caractéristiques Blood Bowl
    @Valid
    @NotNull
    private final PlayerCharacteristic movement; // MA

    @Valid
    @NotNull
    private final PlayerCharacteristic strength; // ST

    @Valid
    @NotNull
    private final PlayerCharacteristic agility; // AG

    @Valid
    @NotNull
    private final PlayerCharacteristic passing; // PA

    @Valid
    @NotNull
    private final PlayerCharacteristic armourValue; // AV

    // Compétences et accès
    @Valid
    private final List<SkillRefLight> skills;

    @Valid
    private final SkillAccessCategories primaryAccess;

    @Valid
    private final SkillAccessCategories secondaryAccess;

    // Méthodes métier
    public boolean hasSkill(String skillUid) {
        return skills != null && skills.stream().anyMatch(s -> s.getUid().equals(skillUid));
    }

    public boolean hasSkills() {
        return skills != null && !skills.isEmpty();
    }

    public boolean hasPrimaryAccess(String category) {
        return primaryAccess != null && primaryAccess.contains(category);
    }

    public boolean hasSecondaryAccess(String category) {
        return secondaryAccess != null && secondaryAccess.contains(category);
    }

    public int getCostInGold() {
        return cost * 1000;
    }
}
