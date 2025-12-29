package com.bloodbowlclub.team_building.domain.events;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.team_building.domain.roster.PlayerDefinition;
import com.bloodbowlclub.team_building.domain.team.RosterSelectedTeam;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlayerHiredEvent extends DomainEvent {

    @NotNull
    @Valid
    private PlayerDefinition player;

    @NotNull
    @Valid
    private RosterSelectedTeam team;


    public PlayerHiredEvent(RosterSelectedTeam team, PlayerDefinition player) {
        super(team.getId());
        this.team = team;
        this.player = player;
    }

    @Override
    public Result<AggregateRoot> applyTo(AggregateRoot aggregate) {
        return aggregate.apply(this);
    }
}
