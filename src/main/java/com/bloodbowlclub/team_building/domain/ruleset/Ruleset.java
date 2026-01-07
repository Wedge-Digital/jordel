package com.bloodbowlclub.team_building.domain.ruleset;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.TranslatableMessage;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@Getter
public class Ruleset extends AggregateRoot {
    @Valid
    @NotNull
    @JsonIgnore
    RulesetID rulesetID;

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

    public Result<CreationBudget> getCreationBudget(Roster candidate) {
        List<RosterTier> targetTier = tierList.stream().filter(t -> t.contains(candidate)).toList();
        if (targetTier.isEmpty()) {
           return Result.failure(
                   new TranslatableMessage(
                           "team_creation.roster_not_present_in_ruleset",
                           candidate.getName(), getName()
                   ),
                   ErrorCode.UNPROCESSABLE_ENTITY
           );
        }
        return Result.success(targetTier.getFirst().getTeamBudget());

    }
}
