package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Data
@SuperBuilder
@NoArgsConstructor
@Getter
public class TeamCreationRuleset extends AggregateRoot {
    @Valid
    @NotNull
    @JsonIgnore
    TeamCreationRulesetID rulesetID;

    @Valid
    @NotNull
    RulesetName name;

    @Valid
    @NotNull
    List<RosterTier> tierList;

    @Override
    public String getId() {
        return rulesetID.toString();
    }

    public boolean isRosterNotAllowed(Roster candidate) {
        return !isRosterAllowed(candidate);
    }

    public boolean isRosterAllowed(Roster candidate) {
        if (this.tierList == null) {
            return false;
        }
        return tierList.stream().filter(tier -> tier.contains(candidate)).toList().isEmpty() == false;
    }
}
