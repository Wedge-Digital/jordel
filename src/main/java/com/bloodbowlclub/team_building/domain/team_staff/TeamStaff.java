package com.bloodbowlclub.team_building.domain.team_staff;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CUSTOM,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(com.bloodbowlclub.lib.io.serialization.resolvers.TeamStaffTypeIdResolver.class)
@Data
@SuperBuilder
@NoArgsConstructor
public class TeamStaff extends AggregateRoot {
    @Valid
    @NotNull
    @JsonIgnore
    private StaffID staffId;

    @Valid
    @NotNull
    private StaffName name;

    @Valid
    @NotNull
    private StuffPrice price;

    @Valid
    @NotNull
    private StaffMaxQuantity maxQuantity;

    @Override
    public String getId() {
        return staffId.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamStaff)) return false;
        TeamStaff roster = (TeamStaff) o;
        return this.staffId.equals(roster.staffId);
    }

    @Override
    public int hashCode() {
        return staffId != null ? staffId.hashCode() : 0;
    }
}
