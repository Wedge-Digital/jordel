package com.bloodbowlclub.team_building.domain.events;


import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.team_building.domain.BaseTeam;
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
public class DraftTeamRegisteredEvent extends TeamCreationEvent {

    public DraftTeamRegisteredEvent(BaseTeam team) {
        super(team);
    }

    @Override
    public Result<AggregateRoot> applyTo(AggregateRoot aggregate) {
        return aggregate.apply(this);
    }
}
