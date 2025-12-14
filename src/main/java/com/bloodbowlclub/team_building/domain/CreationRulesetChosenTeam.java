package com.bloodbowlclub.team_building.domain;

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
public class CreationRulesetChosenTeam extends DraftTeam {

    @NotNull
    @Valid
    TeamCreationRuleset ruleset;

    public CreationRulesetChosenTeam(BaseTeam team, TeamCreationRuleset ruleset) {
        super(team);
        this.ruleset = ruleset;
    }

    @Override
    @JsonIgnore
    public boolean isRulesetChosen() {
        return true;
    }

    //===============================================================================================================
    //
    // Méthodes métier
    //
    //===============================================================================================================

}
