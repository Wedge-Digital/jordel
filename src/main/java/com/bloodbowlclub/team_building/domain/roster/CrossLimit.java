package com.bloodbowlclub.team_building.domain.roster;


import com.bloodbowlclub.lib.domain.Entity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@SuperBuilder
@Getter
public class CrossLimit extends Entity {

    @Valid
    @NotNull
    CrossLimitID crossLimitID;

    @Positive
    int limit;

    @NotEmpty
    List<PlayerDefinition> limitedPlayers;

    @Override
    public String getId() {
        return crossLimitID.toString();
    }

    public boolean playerIsNotLimited(PlayerDefinition player) {
        if (limitedPlayers == null) {
            return true;
        }

        return !limitedPlayers.contains(player);
    }
}
