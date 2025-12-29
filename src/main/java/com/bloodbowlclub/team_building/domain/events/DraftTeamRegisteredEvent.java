package com.bloodbowlclub.team_building.domain.events;


import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.team_building.domain.team.BaseTeam;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DraftTeamRegisteredEvent extends DomainEvent {
    BaseTeam team;

    public DraftTeamRegisteredEvent(BaseTeam team) {
        super(team.getId());
        this.team = team;
    }

    @Override
    public Result<AggregateRoot> applyTo(AggregateRoot aggregate) {
        return aggregate.apply(this);
    }
}
