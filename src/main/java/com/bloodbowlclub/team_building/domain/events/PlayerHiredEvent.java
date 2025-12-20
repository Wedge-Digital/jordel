package com.bloodbowlclub.team_building.domain.events;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.team_building.domain.PlayerDefinition;
import com.bloodbowlclub.team_building.domain.RosterChosenTeam;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Getter
@NoArgsConstructor
public class PlayerHiredEvent extends DomainEvent {

    @NotNull
    @Valid
    private PlayerDefinition player;

    @NotNull
    @Valid
    private RosterChosenTeam team;


    public PlayerHiredEvent(RosterChosenTeam team, PlayerDefinition player) {
        super(team.getId());
        this.team = team;
        this.player = player;
    }

    @Override
    public Result<AggregateRoot> applyTo(AggregateRoot aggregate) {
        return aggregate.apply(this);
    }
}
