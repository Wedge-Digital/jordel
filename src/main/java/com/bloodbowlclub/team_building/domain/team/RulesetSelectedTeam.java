package com.bloodbowlclub.team_building.domain.team;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.TranslatableMessage;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.bloodbowlclub.team_building.domain.ruleset.Ruleset;
import com.bloodbowlclub.team_building.domain.events.RosterChosenEvent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@Getter
public class RulesetSelectedTeam extends DraftTeam {

    @NotNull
    @Valid
    protected Ruleset ruleset;

    protected Roster roster;

    public RulesetSelectedTeam(DraftTeam team, Ruleset ruleset) {
        super(team);
        this.ruleset = ruleset;
    }

    public RulesetSelectedTeam(RulesetSelectedTeam team) {
        super(team);
        this.ruleset = team.ruleset;
    }

    @Override
    @JsonIgnore
    public boolean isRulesetChosen() {
        return ruleset != null;
    }

    //===============================================================================================================
    //
    // Méthodes métier
    //
    //===============================================================================================================
    public ResultMap<Void> chooseRoster(Roster roster) {
        if (!this.isRulesetChosen()) {
            return ResultMap.failure(
                    "team",
                    new TranslatableMessage("team_creation.no_ruleset"),
                    ErrorCode.UNPROCESSABLE_ENTITY
            );
        }

        if (ruleset.isRosterNotAllowed(roster)) {
            return ResultMap.failure(
                    "team",
                    new TranslatableMessage(
                            "team_creation.roster_not_allowed",
                            roster.getId(), roster.getName(), ruleset.getId(), ruleset.getName()
                    ),
                    ErrorCode.INTERNAL_ERROR
            );
        }

        if (roster.isNotValid()) {
            return roster.validationErrors();
        }

        RosterChosenEvent event = new RosterChosenEvent(this, roster);
        this.addEvent(event);
        this.roster = roster;

        return ResultMap.success(null);
    }

    //===============================================================================================================
    //
    // Application des évènements
    //
    //===============================================================================================================

    public Result<AggregateRoot> apply(RosterChosenEvent event) {
        RosterSelectedTeam draftTeam = new RosterSelectedTeam(event.getTeam(), event.getRoster());
        return Result.success(draftTeam);
    }



}
