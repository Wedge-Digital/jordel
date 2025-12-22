package com.bloodbowlclub.team_building.domain.team;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.team_building.domain.events.PlayerRemovedEvent;
import com.bloodbowlclub.team_building.domain.events.RosterChosenEvent;
import com.bloodbowlclub.team_building.domain.ruleset.CreationBudget;
import com.bloodbowlclub.team_building.domain.roster.PlayerDefinition;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.bloodbowlclub.team_building.domain.events.PlayerHiredEvent;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.bloodbowlclub.shared.constants.MAX_PLAYER_COUNT;

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
public class RosterSelectedTeam extends RulesetSelectedTeam {

    @NotNull
    List<PlayerDefinition> hiredPlayers = new ArrayList<>();

    public RosterSelectedTeam(RulesetSelectedTeam team, Roster roster) {
        super(team);
        this.roster = roster;
    }

    @Override
    @JsonIgnore
    public boolean isRosterChosen() {
        return true;
    }

    @JsonIgnore
    public int getHiredPlayerCount() {
        return this.hiredPlayers.size();
    }

    @JsonIgnore
    private boolean isNotHired(PlayerDefinition player) {
        if (this.hiredPlayers == null || this.hiredPlayers.isEmpty()) {
            return true;
        }
        return this.hiredPlayers.stream().filter(
                p-> p.equals(player)
        ).toList().isEmpty();
    }

    private ResultMap<Void> removePlayer(PlayerDefinition player) {
        for (Iterator<PlayerDefinition> it = this.hiredPlayers.iterator(); it.hasNext(); ) {
            PlayerDefinition p = it.next();
            if (p.equals(player)) {
                it.remove();
                PlayerRemovedEvent evt = new PlayerRemovedEvent(this, player);
                this.addEvent(evt);
                return ResultMap.success(null);
            }
        }
        return ResultMap.failure("player_removal", "", ErrorCode.UNKNOWN_ERROR);
    }

    //===============================================================================================================
    //
    // Méthodes métier
    //
    //===============================================================================================================

    boolean maxPlayerHiredReached() {
        return this.getHiredPlayerCount() >= MAX_PLAYER_COUNT;
    }

    public ResultMap<Void> hireManyPlayers(List<PlayerDefinition> hiringList, MessageSource msg) {
        List<ResultMap<Void>> results = hiringList.stream()
                .map(playerToHire -> hirePlayer(playerToHire, msg))
                .toList();

        return results.stream()
                .reduce(ResultMap.success(null), ResultMap::combine);
    }

    ResultMap<Void> checkPlayerIsAllowed(PlayerDefinition player, MessageSource msg) {
        if (roster.doesNotContainPlayer(player)) {
            return ResultMap.failure("team", msg.getMessage(
                    "team_creation.hire_player_impossible",
                    new Object[]{roster.getId(),roster.getName(), player.getId(), player.getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        return ResultMap.success(null);
    }

    ResultMap<Void> checkMaxTotalPlayerIsNotReached(MessageSource msg) {
        if (this.hiredPlayers != null && maxPlayerHiredReached()) {
            return ResultMap.failure("team", msg.getMessage(
                    "team_creation.max_players_reached",
                    new Object[]{getId(), getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        return ResultMap.success(null);
    }

    ResultMap<Void> checkMaxPlayerOfTypeIsNotReached(PlayerDefinition player, MessageSource msg) {
        if (this.hiredPlayers == null) {
            return ResultMap.success(null);
        }
        int hiredPlayersOfType = this.hiredPlayers.stream().filter(
                p -> p.equals(player)
        ).toList().size();

        if (hiredPlayersOfType >= player.getMaxQuantity().getValue()) {
            return ResultMap.failure(
                    "team",
                    msg.getMessage("team_creation.max_players_of_type_reached",
                    new Object[]{getId(),getName(), player.getId(),player.getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        return ResultMap.success(null);
    }

    ResultMap<Void> checkCrosslimits(PlayerDefinition player, MessageSource msg) {
        if (this.hiredPlayers == null) {
            return ResultMap.success(null);
        }

        if (roster.hasNoCrossLimits()) {
            return ResultMap.success(null);
        }

        if (roster.getCrossLimit().playerIsNotLimited(player)) {
            return ResultMap.success(null);
        }

        int limitedPlayerCount = this.hiredPlayers.stream().filter(
                p -> this.roster.getCrossLimit().getLimitedPlayers().contains(p)
        ).toList().size();

        if (limitedPlayerCount >= this.roster.getCrossLimit().getLimit()) {
            return ResultMap.failure(
                    "team",
                    msg.getMessage("team_creation.cross_limit_failed",
                    new Object[]{getId(),getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        return ResultMap.success(null);
    }

    ResultMap<Void> checkBudget(PlayerDefinition player, MessageSource msg) {
        if (this.hiredPlayers == null || this.hiredPlayers.isEmpty()) {
            return ResultMap.success(null);
        }
        Result<CreationBudget> findCreationBudget = this.ruleset.getCreationBudget(this.roster, msg);
        if (findCreationBudget.isFailure()) {
            return ResultMap.failure("team", findCreationBudget.getError(), ErrorCode.UNPROCESSABLE_ENTITY);
        }

        CreationBudget budget = findCreationBudget.getValue();

        int playerCost = this.hiredPlayers.stream().map(
                p -> p.getPrice().getValue()
        ).reduce(0, Integer::sum);

        int remainingBudget = budget.getValue() - playerCost;
        if (remainingBudget < player.getPrice().getValue()) {
            return ResultMap.failure(
                    "team",
                    msg.getMessage("team_creation.insufficient_budget",
                            new Object[]{getId(), getName(), player.getId(),player.getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        return ResultMap.success(null);
    }

    public ResultMap<Void> hirePlayer(PlayerDefinition player, MessageSource msg) {
        if (isNotValid()) {
            return ResultMap.failure(
                    "team",
                    msg.getMessage("team_creation.invalid_team",
                            new Object[]{getId(),getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }

        if (player.isNotValid()) {
            return ResultMap.failure(
                    "team",
                    msg.getMessage("team_creation.invalid_player",
                    new Object[]{player.getId(),player.getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }

        ResultMap<Void> playerCheck = checkPlayerIsAllowed(player, msg);
        ResultMap<Void> playerTotalCheck = checkMaxTotalPlayerIsNotReached(msg);
        ResultMap<Void> playerOfTypeCheck = checkMaxPlayerOfTypeIsNotReached(player, msg);
        ResultMap<Void> crossLimitCheck = checkCrosslimits(player, msg);
        ResultMap<Void> budgetCheck = checkBudget(player, msg);

        ResultMap<Void> combined = ResultMap.combine(playerCheck, playerTotalCheck, playerOfTypeCheck, crossLimitCheck, budgetCheck);
        if (combined.isFailure()) {
            return combined;
        }

        if (this.hiredPlayers == null) {
            this.hiredPlayers = new ArrayList<>();
        }

        this.hiredPlayers.add(player);

        PlayerHiredEvent event = new PlayerHiredEvent(this, player);
        this.addEvent(event);

        return ResultMap.success(null);
    }

    @Override
    public ResultMap<Void> chooseRoster(Roster roster, MessageSource msg) {
        if (roster == this.roster) {
            return ResultMap.success(null);
        }
        this.hiredPlayers = new ArrayList<>();
        ResultMap<Void> rosterSlection = super.chooseRoster(roster, msg);
        if (rosterSlection.isFailure()) {
            return rosterSlection;
        }
        return rosterSlection;
    }

    public ResultMap<Void> removePlayer(PlayerDefinition player, MessageSource msg) {
        if (isNotHired(player)) {
            return ResultMap.failure(
                    "team",
                    msg.getMessage("team_creation.player_not_hired",
                            new Object[]{player.getId(),player.getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        ResultMap<Void> playerRemoval = removePlayer(player);
        if (playerRemoval.isSuccess()) {
           return playerRemoval;
        }
        return ResultMap.failure("team", "abormal player remove termination", ErrorCode.INTERNAL_ERROR);
    }

    //===============================================================================================================
    //
    // Application des évènements
    //
    //===============================================================================================================

    @Override
    public Result<AggregateRoot> apply(PlayerHiredEvent event) {
        if (this.hiredPlayers == null) {
            this.hiredPlayers = new ArrayList<>();
        }
        this.hiredPlayers.add(event.getPlayer());
        return Result.success(this);
    }

    @Override
    public Result<AggregateRoot> apply(RosterChosenEvent event) {
        Result<AggregateRoot> sup = super.apply(event);
        if (sup.isSuccess()) {
            RosterSelectedTeam team = (RosterSelectedTeam) sup.getValue();
            team.hiredPlayers = new ArrayList<>();
            return  Result.success(team);
        }
        return sup;
    }


    @Override
    public Result<AggregateRoot> apply(PlayerRemovedEvent event) {
        ResultMap<Void> playerRemoval = removePlayer(event.getPlayer());
        if (playerRemoval.isFailure()) {
            return Result.failure(playerRemoval.getError(), ErrorCode.INTERNAL_ERROR);
        }
        return Result.success(this);
    }

}
