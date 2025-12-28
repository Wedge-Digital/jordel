package com.bloodbowlclub.team_building.domain.team;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.team_building.domain.events.*;
import com.bloodbowlclub.team_building.domain.ruleset.CreationBudget;
import com.bloodbowlclub.team_building.domain.roster.PlayerDefinition;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.bloodbowlclub.team_building.domain.team_staff.TeamStaff;
import com.bloodbowlclub.team_building.domain.team_staff.staff_detail.Cheerleaders;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
import static com.bloodbowlclub.shared.constants.MAX_REROLL_COUNT;

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

    @PositiveOrZero
    @Max(value = MAX_REROLL_COUNT, message = "{reroll_count.max}")
    private int rerollCount = 0;

    @NotNull
    List<PlayerDefinition> hiredPlayers = new ArrayList<>();

    @NotNull
    List<TeamStaff> hiredStaff = new ArrayList<>();


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
    private boolean playerIsNotHired(PlayerDefinition player) {
        if (this.hiredPlayers == null || this.hiredPlayers.isEmpty()) {
            return true;
        }
        return !this.hiredPlayers.contains(player);
    }

    @JsonIgnore
    private boolean staffIsNotPurchased(TeamStaff staff) {
        if (this.hiredStaff == null || this.hiredStaff.isEmpty()) {
            return true;
        }
        return !this.hiredStaff.contains(staff);
    }

    private ResultMap<Void> removePlayer(PlayerDefinition player) {
        for (Iterator<PlayerDefinition> it = this.hiredPlayers.iterator(); it.hasNext(); ) {
            PlayerDefinition p = it.next();
            if (p.equals(player)) {
                it.remove();
                return ResultMap.success(null);
            }
        }
        return ResultMap.failure("player_removal", "", ErrorCode.UNKNOWN_ERROR);
    }

    private ResultMap<Void> removeStaff(TeamStaff staff) {
        for (Iterator<TeamStaff> it = this.hiredStaff.iterator(); it.hasNext(); ) {
            TeamStaff s = it.next();
            if (s.equals(staff)) {
                it.remove();
                return ResultMap.success(null);
            }
        }
        return ResultMap.failure("player_removal", "", ErrorCode.UNKNOWN_ERROR);
    }

    @JsonIgnore
    public int getStaffBudget() {
        if (this.hiredStaff == null || this.hiredStaff.isEmpty()) {
            return 0;
        }
        return this.hiredStaff.stream().map(
                p -> p.getPrice().getValue()
        ).reduce(0, Integer::sum);
    }

   @JsonIgnore
   public int getRerollBudget() {
        return this.rerollCount * this.roster.getRerollPrice().getValue();
   }

    @JsonIgnore
    public int getCheerleaders() {
        if (this.hiredStaff == null || this.hiredStaff.isEmpty()) {
            return 0;
        }
        return this.hiredStaff.stream().filter(
               s -> s.getClass() == Cheerleaders.class
        ).toList().size();
    }

    @JsonIgnore
    private int getPlayerBudget() {
        return this.hiredPlayers.stream().map(
                p -> p.getPrice().getValue()
        ).reduce(0, Integer::sum);
    }

    public ResultMap<Integer> computeRemainingBudget(MessageSource msg) {
        Result<CreationBudget> findCreationBudget = this.ruleset.getCreationBudget(this.roster, msg);
        if (findCreationBudget.isFailure()) {
            return ResultMap.failure("team.budget", findCreationBudget.getError(), ErrorCode.UNPROCESSABLE_ENTITY);
        }

        int creationBudget = findCreationBudget.getValue().getValue();
        int playerBudget = getPlayerBudget();
        int staffBudget = getStaffBudget();
        int rerollBudget = getRerollBudget();
        int remainingBudget = creationBudget - playerBudget - staffBudget - rerollBudget;
        return ResultMap.success(remainingBudget);
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
            return ResultMap.failure("team.player", msg.getMessage(
                    "team_creation.hire_player_impossible",
                    new Object[]{roster.getId(),roster.getName(), player.getId(), player.getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        return ResultMap.success(null);
    }

    ResultMap<Void> checkMaxTotalPlayerIsNotReached(MessageSource msg) {
        if (this.hiredPlayers != null && maxPlayerHiredReached()) {
            return ResultMap.failure("team.player", msg.getMessage(
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
                    "team.player",
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
                    "team.player",
                    msg.getMessage("team_creation.cross_limit_failed",
                    new Object[]{getId(),getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        return ResultMap.success(null);
    }



    ResultMap<Void> checkBudget(TeamStaff staff, MessageSource msg) {
        ResultMap<Integer> remainingBudgetCalcul = computeRemainingBudget(msg);
        if (remainingBudgetCalcul.isFailure()) {
            return ResultMap.failure(remainingBudgetCalcul.errorMap(), ErrorCode.UNKNOWN_ERROR_CODE);
        }
        int remainingBudget = remainingBudgetCalcul.getValue();

        if (remainingBudget < staff.getPrice().getValue()) {
            return ResultMap.failure(
                    "team.staff",
                    msg.getMessage("team_creation.insufficient_staff_budget",
                            new Object[]{getId(), getName(), staff.getId(),staff.getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        return ResultMap.success(null);
    }


    ResultMap<Void> checkBudget(PlayerDefinition player, MessageSource msg) {
        if (this.hiredPlayers == null || this.hiredPlayers.isEmpty()) {
            return ResultMap.success(null);
        }
        ResultMap<Integer> remainingBudgetCalcul = computeRemainingBudget(msg);
        if (remainingBudgetCalcul.isFailure()) {
            return ResultMap.failure(remainingBudgetCalcul.errorMap(), ErrorCode.UNKNOWN_ERROR_CODE);
        }
        int remainingBudget = remainingBudgetCalcul.getValue();

        if (remainingBudget < player.getPrice().getValue()) {
            return ResultMap.failure(
                    "team.player",
                    msg.getMessage("team_creation.insufficient_budget",
                            new Object[]{getId(), getName(), player.getId(),player.getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        return ResultMap.success(null);
    }

    ResultMap<Void> checkRerollBudget(int rerollCount, MessageSource msg) {
        ResultMap<Integer> remainingBudgetCalcul = computeRemainingBudget(msg);
        if (remainingBudgetCalcul.isFailure()) {
            return ResultMap.failure(remainingBudgetCalcul.errorMap(), ErrorCode.UNKNOWN_ERROR_CODE);
        }
        int remainingBudget = remainingBudgetCalcul.getValue();

        int rerollBudget = rerollCount * this.roster.getRerollPrice().getValue();
        if (remainingBudget < rerollBudget) {
            return ResultMap.failure(
                    "team.reroll",
                    msg.getMessage("team_creation.insufficient_reroll_budget",
                            new Object[]{getId(), getName(),rerollCount}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        return ResultMap.success(null);
    }

    private ResultMap<Void> checkStaffLimitIsNotReached(TeamStaff staff, MessageSource msg) {
        if (this.hiredStaff == null || this.hiredStaff.isEmpty()) {
            return ResultMap.success(null);
        }

        int boughtStaffCount = this.hiredStaff.stream().filter(
                s -> s.equals(staff)
        ).toList().size();

        if (boughtStaffCount >= staff.getMaxQuantity().getValue()) {
            return ResultMap.failure(
                    "team.staff",
                    msg.getMessage("team_creation.staff_max_reached",
                            new Object[]{staff.getId(), staff.getName(), staff.getMaxQuantity().getValue(), this.getId(),this.getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        return ResultMap.success(null);
    }

    private ResultMap<Void> checkStaffIsAllowed(TeamStaff staff, MessageSource msg) {
        if (this.roster.getAllowedTeamStaff().contains(staff)) {
           return ResultMap.success(null);
        }
        return ResultMap.failure(
                "team.staff",
                msg.getMessage("team_creation.staff_not_allowed",
                        new Object[]{staff.getId(), staff.getName(), this.getId(),this.getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);

    }
    
    private ResultMap<Void> checkRerollMaxNotReached(int rerollCount, MessageSource msg) {
        int potentialRerolCount = this.rerollCount + rerollCount;
        if (potentialRerolCount > MAX_REROLL_COUNT) {
            return ResultMap.failure("team.reroll",
                    msg.getMessage("team_creation.max_rr_exceeded",
                            new Object[]{getId(), getName(), rerollCount}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
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

    private void resetPurchases() {
        this.hiredPlayers = new ArrayList<>();
        this.rerollCount = 0;
        this.hiredStaff = new ArrayList<>();
    }

    @Override
    public ResultMap<Void> chooseRoster(Roster roster, MessageSource msg) {
        if (roster == this.roster) {
            return ResultMap.success(null);
        }
        ResultMap<Void> rosterSelection = super.chooseRoster(roster, msg);
        if (rosterSelection.isFailure()) {
            return rosterSelection;
        }
        this.resetPurchases();
        return rosterSelection;
    }

    public ResultMap<Void> removePlayer(PlayerDefinition player, MessageSource msg) {
        if (isNotValid()) {
            return ResultMap.failure(
                    "team",
                    msg.getMessage("team_creation.invalid_team",
                            new Object[]{getId(),getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        if (playerIsNotHired(player)) {
            return ResultMap.failure(
                    "team",
                    msg.getMessage("team_creation.player_not_hired",
                            new Object[]{player.getId(),player.getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        ResultMap<Void> playerRemoval = removePlayer(player);
        if (playerRemoval.isSuccess()) {
            PlayerRemovedEvent evt = new PlayerRemovedEvent(this, player);
            this.addEvent(evt);
            return playerRemoval;
        }
        return ResultMap.failure("team", "abormal player remove termination", ErrorCode.INTERNAL_ERROR);
    }

    public ResultMap<Void> buyStaff(TeamStaff staff, MessageSource msg) {
        if (isNotValid()) {
            return ResultMap.failure(
                    "team",
                    msg.getMessage("team_creation.invalid_team",
                            new Object[]{getId(),getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }

        ResultMap<Void> checkStaffLimit = checkStaffLimitIsNotReached(staff, msg);
        ResultMap<Void> checkBudget = checkBudget(staff, msg);
        ResultMap<Void> checkStaff = checkStaffIsAllowed(staff, msg);

        ResultMap<Void> combined = ResultMap.combine(checkStaffLimit, checkBudget, checkStaff);
        if (combined.isFailure()) {
            return combined;
        }

        if (this.hiredStaff == null || this.hiredStaff.isEmpty()) {
            this.hiredStaff = new ArrayList<>();
        }

        this.hiredStaff.add(staff);
        TeamStaffPurchasedEvent evt = new TeamStaffPurchasedEvent(this, staff);
        this.addEvent(evt);
        return ResultMap.success(null);
    }

    public ResultMap<Void> removeStaff(TeamStaff staff, MessageSource msg) {
        if (isNotValid()) {
            return ResultMap.failure(
                    "team",
                    msg.getMessage("team_creation.invalid_team",
                            new Object[]{getId(),getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        if (staffIsNotPurchased(staff)) {
            return ResultMap.failure(
                    "team.staff",
                    msg.getMessage("team_creation.staff_not_purchased",
                            new Object[]{getId(),getName(), staff.getId(), staff.getName()}, Locale.getDefault()), ErrorCode.UNPROCESSABLE_ENTITY);
        }
        ResultMap<Void> staffRemoval = this.removeStaff(staff);
        if (staffRemoval.isSuccess()) {
            TeamStaffRemovedEvent evt = new TeamStaffRemovedEvent(this, staff);
            this.addEvent(evt);
            return staffRemoval;
        }

        return ResultMap.failure("team.staff", "abnormal staff remove termination", ErrorCode.INTERNAL_ERROR);
    }

    public ResultMap<Void> purchaseReroll(int rerollCount,  MessageSource msg ) {
        ResultMap<Void> checkMax = checkRerollMaxNotReached(rerollCount, msg);
        ResultMap<Void> checkBudget = checkRerollBudget(rerollCount, msg);

        ResultMap<Void> combined = ResultMap.combine(checkMax, checkBudget, checkBudget);
        if (combined.isFailure()) {
            return combined;
        }

        this.rerollCount += rerollCount;
        TeamRerollPurchasedEvent evt = new TeamRerollPurchasedEvent(this, rerollCount);
        this.addEvent(evt);
        return ResultMap.success(null);
    }

    public ResultMap<Void> removeReroll(int rerollCount,  MessageSource msg ) {
        if (rerollCount <= 0) {
            return ResultMap.success(null);
        }

        if (rerollCount > this.rerollCount) {
            this.rerollCount = 0;
        } else { this.rerollCount -= rerollCount; }
        TeamRerollRemovedEvent evt = new TeamRerollRemovedEvent(this, rerollCount);
        this.addEvent(evt);
        return ResultMap.success(null);
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
            team.resetPurchases();
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

    @Override
    public Result<AggregateRoot> apply(TeamStaffPurchasedEvent event) {
        if (this.hiredStaff == null) {
            this.hiredStaff = new ArrayList<>();
        }
        this.hiredStaff.add(event.getStaff());
        return Result.success(this);
    }

    @Override
    public Result<AggregateRoot> apply(TeamStaffRemovedEvent event) {
        ResultMap<Void> staffRemoval = removeStaff(event.getStaff());
        if (staffRemoval.isFailure()) {
            return Result.failure(staffRemoval.getError(), ErrorCode.INTERNAL_ERROR);
        }
        return Result.success(this);
    }

    @Override
    public Result<AggregateRoot> apply(TeamRerollPurchasedEvent event) {
        this.rerollCount += event.getRerollCount();
        return Result.success(this);
    }

    @Override
    public Result<AggregateRoot> apply(TeamRerollRemovedEvent event) {
        int rerollToRemove = event.getRerollCount();
        if (rerollToRemove > this.rerollCount) {
            this.rerollCount = 0;
        } else {
            this.rerollCount -= rerollToRemove;
        }
        return Result.success(this);
    }

}
