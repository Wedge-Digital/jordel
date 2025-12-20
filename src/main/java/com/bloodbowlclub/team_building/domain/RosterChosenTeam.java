package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.ResultMap;
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
public class RosterChosenTeam extends CreationRulesetChosenTeam {

    @NotNull
    @Valid
    Roster roster;

    @NotNull
    List<PlayerDefinition> hiredPlayers = new ArrayList<>();

    public RosterChosenTeam(CreationRulesetChosenTeam team, Roster roster) {
        super(team);
        this.roster = roster;
    }

    @Override
    @JsonIgnore
    public boolean isRosterChosen() {
        return true;
    }

    int getHiredPlayerCount() {
        return this.hiredPlayers.size();
    }

    //===============================================================================================================
    //
    // Méthodes métier
    //
    //===============================================================================================================

    boolean maxPlayerHiredReached() {
        return this.getHiredPlayerCount() >= MAX_PLAYER_COUNT;
    }

    ResultMap<Void> hireManyPlayers(List<PlayerDefinition> hiringList, MessageSource msg) {
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
                    new Object[]{roster.getRosterName(), player.name}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        return ResultMap.success(null);
    }

    ResultMap<Void> checkMaxTotalPlayerIsNotReached(MessageSource msg) {
        if (this.hiredPlayers != null && maxPlayerHiredReached()) {
            return ResultMap.failure("team", msg.getMessage(
                    "team_creation.max_players_reached",
                    new Object[]{getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
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
                    new Object[]{getName(), player.getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
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
                    new Object[]{getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        return ResultMap.success(null);
    }

    ResultMap<Void> hirePlayer(PlayerDefinition player, MessageSource msg) {
        ResultMap<Void> playerCheck = checkPlayerIsAllowed(player, msg);
        ResultMap<Void> playerTotalCheck = checkMaxTotalPlayerIsNotReached(msg);
        ResultMap<Void> playerOfTypeCheck = checkMaxPlayerOfTypeIsNotReached(player, msg);
        ResultMap<Void> crossLimitCheck = checkCrosslimits(player, msg);

        ResultMap<Void> combined = ResultMap.combine(playerCheck, playerTotalCheck, playerOfTypeCheck, crossLimitCheck);
        if (combined.isFailure()) {
            return combined;
        }

        if (this.hiredPlayers == null) {
            this.hiredPlayers = new ArrayList<>();
        }

        this.hiredPlayers.add(player);

        return ResultMap.success(null);
    }

}
