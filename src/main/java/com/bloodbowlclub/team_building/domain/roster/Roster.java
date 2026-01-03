package com.bloodbowlclub.team_building.domain.roster;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.shared.roster.RosterID;
import com.bloodbowlclub.shared.roster.RosterName;
import com.bloodbowlclub.team_building.domain.team_staff.TeamStaff;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Objects;

@Data
@SuperBuilder
@NoArgsConstructor
public class Roster extends AggregateRoot {

    @Valid
    @NotNull
    @JsonIgnore
    private RosterID rosterId;

    @Valid
    @NotNull
    private RosterName name;

    @NotEmpty
    @Valid
    private List<PlayerDefinition> playerDefinitions;

    @NotEmpty
    @Valid
    private List<TeamStaff> allowedTeamStaff;

    @Valid
    private List<CrossLimit> crossLimits;

    @Valid
    @NotNull
    private RerollBasePrice rerollPrice;

    @Override
    public String getId() {
        return rosterId.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Roster roster = (Roster) o;
        return this.rosterId.equals(roster.rosterId);
        // ou une autre logique (name, etc.) selon ce qui définit l’égalité
    }

    @Override
    public int hashCode() {
        return Objects.hash(rosterId.toString());
    }

    public boolean hasNoCrossLimits() {
        return this.crossLimits == null || this.crossLimits.isEmpty();
    }

    public boolean hasCrossLimits() {
        return !hasNoCrossLimits();
    }

    //===============================================================================================================
    //
    // Méthodes métier
    //
    //===============================================================================================================

    public boolean doesNotContainPlayer(PlayerDefinition player) {
        if (this.playerDefinitions == null || this.playerDefinitions.isEmpty()) {
            return true;
        }
        return this.playerDefinitions.stream().filter(lines -> lines.equals(player)).toList().isEmpty();
    }


    //===============================================================================================================
    //
    // Application d'évènements
    //
    //===============================================================================================================


}
