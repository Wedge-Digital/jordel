package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.shared.shared.cloudinary_url.CloudinaryUrl;
import com.bloodbowlclub.shared.team.TeamID;
import com.bloodbowlclub.shared.team.TeamName;
import com.bloodbowlclub.team_building.domain.commands.RegisterNewTeamCommand;
import com.bloodbowlclub.team_building.domain.events.DraftTeamRegisteredEvent;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class BaseTeam extends AggregateRoot {

    @Valid
    @NotNull
    @JsonIgnore
    private TeamID teamId;

    @Valid
    @NotNull
    private TeamName name;

    @Valid
    @NotNull
    private CloudinaryUrl logoUrl;

    public BaseTeam(TeamID id, TeamName name, CloudinaryUrl logo) {
        this.teamId = id;
        this.name = name;
        this.logoUrl = logo;
    }

    //===============================================================================================================
    //
    // Méthodes métier
    //
    //===============================================================================================================

    public ResultMap<Void> registerNewTeam(RegisterNewTeamCommand command) {
        teamId = new TeamID(command.getTeamId());
        name = new TeamName(command.getTeamName());
        logoUrl = new CloudinaryUrl(command.getTeamLogo());

        if (isNotValid()) {
            return validationErrors();
        }

        DraftTeamRegisteredEvent event = new DraftTeamRegisteredEvent(this);
        this.addEvent(event);

        return ResultMap.success(null);
    }


    //===============================================================================================================
    //
    // Application des évènements
    //
    //===============================================================================================================

    public Result<AggregateRoot> apply(DraftTeamRegisteredEvent event) {
        DraftTeam draftTeam = new DraftTeam(event.getTeam());
        return Result.success(draftTeam);
    }

    //===============================================================================================================

    @JsonIgnore
    public boolean isDraftTeam() {
        return false;
    }

    @JsonIgnore
    public boolean isRosterChosen() {
        return false;
    }

    @JsonIgnore
    public boolean isRulesetChosen() {
        return false;
    }

    @Override
    public String getId() {
        return teamId.toString();
    }
}
