package com.bloodbowlclub.team_building.domain.events;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.team_building.domain.team.RulesetSelectedTeam;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RosterChosenEvent extends DomainEvent {
    RulesetSelectedTeam team;
    Roster roster;

    public RosterChosenEvent(RulesetSelectedTeam team, Roster roster) {
        super(team.getId());
        this.team = team;
        this.roster = roster;

    }

    @Override
    public Result<AggregateRoot> applyTo(AggregateRoot aggregate) {
        return aggregate.apply(this);
    }
}
