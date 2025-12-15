package com.bloodbowlclub.team_building.domain.events;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.team_building.domain.DraftTeam;
import com.bloodbowlclub.team_building.domain.TeamCreationRuleset;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Getter
@NoArgsConstructor
public class CreationRulesetSelectedEvent extends DomainEvent {
    private DraftTeam team;
    private TeamCreationRuleset ruleset;

    public CreationRulesetSelectedEvent(DraftTeam team, TeamCreationRuleset ruleset) {
        super(team.getId());
        this.team = team;
        this.ruleset = ruleset;
    }

    @Override
    public Result<AggregateRoot> applyTo(AggregateRoot aggregate) {
        return aggregate.apply(this);
    }

}
