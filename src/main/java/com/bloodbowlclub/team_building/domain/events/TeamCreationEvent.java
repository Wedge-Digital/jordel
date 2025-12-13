package com.bloodbowlclub.team_building.domain.events;

import com.bloodbowlclub.lib.domain.events.DomainEvent;
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
public abstract class TeamCreationEvent  extends DomainEvent {
    private BaseTeam team;

    public TeamCreationEvent(BaseTeam team){
        super(team.getId());
        this.team = team;
    }

}
