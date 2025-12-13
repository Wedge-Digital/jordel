package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.shared.shared.cloudinary_url.CloudinaryUrl;
import com.bloodbowlclub.shared.team.TeamID;
import com.bloodbowlclub.shared.team.TeamName;
import com.bloodbowlclub.team_building.domain.commands.RegisterNewTeamCommand;
import com.bloodbowlclub.team_building.domain.events.DraftTeamRegisteredEvent;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Data
@SuperBuilder
@NoArgsConstructor
public class DraftTeam extends BaseTeam {

    public DraftTeam(BaseTeam team) {
        super(team.getTeamId(), team.getName(), team.getLogoUrl());
    }

    public boolean isDraftTeam() {
        return true;
    }

}
