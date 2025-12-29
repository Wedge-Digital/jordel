package com.bloodbowlclub.team_building.domain.events;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.team_building.domain.team.DraftTeam;
import com.bloodbowlclub.team_building.domain.ruleset.Ruleset;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RulesetSelectedEvent extends DomainEvent {
    private DraftTeam team;
    private Ruleset ruleset;

    public RulesetSelectedEvent(DraftTeam team, Ruleset ruleset) {
        super(team.getId());
        this.team = team;
        this.ruleset = ruleset;
    }

    @Override
    public Result<AggregateRoot> applyTo(AggregateRoot aggregate) {
        return aggregate.apply(this);
    }

}
