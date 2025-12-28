package com.bloodbowlclub.team_building.domain.events;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.team_building.domain.team.RosterSelectedTeam;
import com.bloodbowlclub.team_building.domain.team_staff.TeamStaff;
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
public class TeamRerollRemovedEvent extends DomainEvent {

    @NotNull
    @Valid
    private RosterSelectedTeam team;

    private int rerollCount;

    public TeamRerollRemovedEvent(RosterSelectedTeam team, int rerollCount) {
        super(team.getId());
        this.team = team;
        this.rerollCount = rerollCount;
    }

    @Override
    public Result<AggregateRoot> applyTo(AggregateRoot aggregate) {
        return aggregate.apply(this);
    }
}
