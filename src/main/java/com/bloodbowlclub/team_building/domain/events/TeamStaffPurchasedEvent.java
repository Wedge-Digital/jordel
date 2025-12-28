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
public class TeamStaffPurchasedEvent extends DomainEvent {

    @NotNull
    @Valid
    private RosterSelectedTeam team;

    @NotNull
    @Valid
    private TeamStaff staff;

    public TeamStaffPurchasedEvent(RosterSelectedTeam team, TeamStaff staff) {
        super(team.getId());
        this.team = team;
        this.staff = staff;
    }

    @Override
    public Result<AggregateRoot> applyTo(AggregateRoot aggregate) {
        return aggregate.apply(this);
    }
}
