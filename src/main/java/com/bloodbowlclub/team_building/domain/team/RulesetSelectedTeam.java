package com.bloodbowlclub.team_building.domain.team;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.bloodbowlclub.team_building.domain.ruleset.Ruleset;
import com.bloodbowlclub.team_building.domain.events.RosterChosenEvent;
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
public class RulesetSelectedTeam extends DraftTeam {

    @NotNull
    @Valid
    protected Ruleset ruleset;

    protected Roster roster;

    public RulesetSelectedTeam(DraftTeam team, Ruleset ruleset) {
        super(team);
        this.ruleset = ruleset;
    }

    public RulesetSelectedTeam(RulesetSelectedTeam team) {
        super(team);
        this.ruleset = team.ruleset;
    }

    @Override
    @JsonIgnore
    public boolean isRulesetChosen() {
        return ruleset != null;
    }

    //===============================================================================================================
    //
    // Méthodes métier
    //
    //===============================================================================================================
    public ResultMap<Void> chooseRoster(Roster roster, MessageSource msg) {
        if (!this.isRulesetChosen()) {
            return ResultMap.failure("team", msg.getMessage("team_creation.no_ruleset", new Object[]{}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }

        if (ruleset.isRosterNotAllowed(roster)) {
            return ResultMap.failure("team",
                    msg.getMessage("team_creation.roster_not_allowed",
                            new Object[]{roster.getId(), roster.getName(), ruleset.getId(), ruleset.getName()}, Locale.getDefault()), ErrorCode.INTERNAL_ERROR);
        }

        if (roster.isNotValid()) {
            return roster.validationErrors();
        }

        RosterChosenEvent event = new RosterChosenEvent(this, roster);
        this.addEvent(event);
        this.roster = roster;

        return ResultMap.success(null);
    }

    //===============================================================================================================
    //
    // Application des évènements
    //
    //===============================================================================================================

    public Result<AggregateRoot> apply(RosterChosenEvent event) {
        RosterSelectedTeam draftTeam = new RosterSelectedTeam(event.getTeam(), event.getRoster());
        return Result.success(draftTeam);
    }



}
