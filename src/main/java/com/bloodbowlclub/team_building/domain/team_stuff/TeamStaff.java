package com.bloodbowlclub.team_building.domain.team_stuff;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Data
@SuperBuilder
@NoArgsConstructor
public class TeamStaff extends AggregateRoot {
    @Valid
    @NotNull
    @JsonIgnore
    private StuffID stuffId;

    @Valid
    @NotNull
    private StuffName name;

    @Valid
    @NotNull
    private StuffPrice price;

    @Valid
    @NotNull
    private StuffMaxQuantity maxQuantity;

    @Override
    public String getId() {
        return stuffId.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamStaff roster = (TeamStaff) o;
        return this.stuffId.equals(roster.stuffId);
    }
}
