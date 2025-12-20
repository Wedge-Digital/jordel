package com.bloodbowlclub.team_building.domain.ruleset;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

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

    public Result<CreationBudget> getCreationBudget(Roster candidate, MessageSource msg) {
        List<RosterTier> targetTier = tierList.stream().filter(t -> t.contains(candidate)).toList();
        if (targetTier.isEmpty()) {
           return Result.failure(msg.getMessage(
                   "team_creation.roster_not_present_in_ruleset",
                   new Object[]{candidate.getName(), getName()}, Locale.getDefault()
           ), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        return Result.success(targetTier.getFirst().getTeamBudget());

    }
}
