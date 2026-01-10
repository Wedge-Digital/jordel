package com.bloodbowlclub.reference.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Représentation d'un roster dans les données de référence.
 * Immutable, chargée depuis JSON au démarrage.
 * ISOLÉE de team_building.domain.roster.Roster.
 */
@Getter
@Builder
public class RosterRef {

    @NotNull
    private final String uid;

    @NotNull
    private final String name;

    @NotNull
    private final Integer rerollCost; // Coût en milliers de gold

    @Valid
    private final RosterTier tier;

    @Valid
    private final SpecialRulesList specialRules;

    @NotEmpty
    @Valid
    private final List<PlayerDefinitionRef> availablePlayers;

    @NotEmpty
    private final List<String> allowedStaffUids;

    @Valid
    private final List<CrossLimitRef> crossLimits;

    // Méthodes métier
    public boolean hasNoCrossLimits() {
        return this.crossLimits == null || this.crossLimits.isEmpty();
    }

    public boolean hasCrossLimits() {
        return !hasNoCrossLimits();
    }

    public boolean hasSpecialRules() {
        return this.specialRules != null && !this.specialRules.isEmpty();
    }

    public Optional<PlayerDefinitionRef> findPlayerByUid(String playerUid) {
        if (availablePlayers == null) {
            return Optional.empty();
        }
        return availablePlayers.stream()
                .filter(p -> p.getUid().equals(playerUid))
                .findFirst();
    }

    public boolean canHirePlayer(String playerUid) {
        return findPlayerByUid(playerUid).isPresent();
    }

    public int getRerollCostInGold() {
        return rerollCost * 1000;
    }

    public List<PlayerDefinitionRef> getAvailablePlayers() {
        return availablePlayers != null ?
            Collections.unmodifiableList(availablePlayers) :
            Collections.emptyList();
    }
}
