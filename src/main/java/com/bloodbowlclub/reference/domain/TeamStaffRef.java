package com.bloodbowlclub.reference.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 * Représentation d'un staff d'équipe dans les données de référence.
 * ISOLÉE de team_building.domain.team_staff.TeamStaff.
 */
@Getter
@Builder
public class TeamStaffRef {

    @NotNull
    private final String uid;

    @NotNull
    private final String name;

    @NotNull
    private final Integer price; // Prix en milliers de gold

    @NotNull
    private final Integer maxQuantity;

    private final String description;

    public int getPriceInGold() {
        return price * 1000;
    }
}
