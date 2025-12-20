package com.bloodbowlclub.team_building.domain.team;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.team_building.domain.ruleset.Ruleset;
import com.bloodbowlclub.team_building.domain.events.CreationRulesetSelectedEvent;
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


    public ResultMap<Void> selectCreationRuleset(Ruleset ruleset) {
        if (ruleset.isNotValid()) {
            return ruleset.validationErrors();
        }

        CreationRulesetSelectedEvent event = new CreationRulesetSelectedEvent(this, ruleset);
        this.addEvent(event);

        return ResultMap.success(null);

    }

    //===============================================================================================================
    //
    // Application des évènements
    //
    //===============================================================================================================


    public Result<AggregateRoot> apply(CreationRulesetSelectedEvent event) {
        CreationRulesetChosenTeam rulesetChosentTeam = new CreationRulesetChosenTeam(event.getTeam(), event.getRuleset());
        return Result.success(rulesetChosentTeam);
    }

}
