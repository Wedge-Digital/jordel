package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.team_building.domain.events.RosterChosenEvent;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Data
@SuperBuilder
@NoArgsConstructor
public class DraftTeam extends BaseTeam {

    public DraftTeam(BaseTeam team) {
        super(team.getTeamId(), team.getName(), team.getLogoUrl());
    }

    @Override
    public boolean isDraftTeam() {
        return true;
    }

    //===============================================================================================================
    //
    // Méthodes métier
    //
    //===============================================================================================================

    public ResultMap<Void> chooseRoster(Roster roster) {

        if (roster.isNotValid()) {
            return validationErrors();
        }

        RosterChosenEvent event = new RosterChosenEvent(this, roster);
        this.addEvent(event);

        return ResultMap.success(null);
    }

    //===============================================================================================================
    //
    // Application des évènements
    //
    //===============================================================================================================

    public Result<AggregateRoot> apply(RosterChosenEvent event) {
        RosterChosenTeam draftTeam = new RosterChosenTeam(event.getTeam(), event.getRoster());
        return Result.success(draftTeam);
    }

}
