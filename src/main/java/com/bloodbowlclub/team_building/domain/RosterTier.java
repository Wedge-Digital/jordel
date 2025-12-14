package com.bloodbowlclub.team_building.domain;

import  com.bloodbowlclub.lib.domain.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@NoArgsConstructor
@SuperBuilder
public class RosterTier extends Entity {

    @Valid
    @NotNull
    @JsonIgnore
    TierID tierID;

    @Valid
    @NotNull
    TierName name;

    @Valid
    @NotNull
    List<Roster> rosterList;

    public String getId() {
        return this.tierID.toString();
    }

    public boolean contains(Roster candidate) {
        return this.rosterList.contains(candidate);
    }
}
