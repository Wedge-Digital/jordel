package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.team_building.domain.events.*;
import com.bloodbowlclub.team_building.domain.roster.PlayerDefinition;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.bloodbowlclub.team_building.domain.ruleset.Ruleset;
import com.bloodbowlclub.team_building.domain.team.RosterSelectedTeam;
import com.bloodbowlclub.test_utilities.team_creation.PlayerDefinitionCreator;
import com.bloodbowlclub.test_utilities.team_creation.RosterCreator;
import com.bloodbowlclub.test_utilities.team_creation.RulesetCreator;
import com.bloodbowlclub.test_utilities.team_creation.StaffCreator;
import com.bloodbowlclub.test_utilities.team_creation.TeamCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class HirePlayerTest extends TestCase {

    TeamCreator teamCreator = new TeamCreator();
    RosterCreator rosterCreator = new RosterCreator();
    RulesetCreator rulesetCreator = new RulesetCreator();
    PlayerDefinitionCreator playerCreator = new PlayerDefinitionCreator();
    StaffCreator staffCreator = new StaffCreator();

    @Test
    @DisplayName("hiring a player should be ok, if no player are previously drafted, and player definition exist in roster")
    void testHirePlayerOk() {
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterSelectedTeam rcTeam =  teamCreator.createChaosTeam(chaosPact);
        PlayerDefinition toHire = chaosPact.getPlayerDefinitions().getFirst();
        ResultMap<Void> hiring = rcTeam.hirePlayer(toHire, messageSource);
        Assertions.assertTrue(hiring.isSuccess());
    }

    @Test
    @DisplayName("hiring a player should be failing, if this player definition doesn't exist in roster")
    void testHirePlayerWithEmptyRosterFails() {
        RosterSelectedTeam rcTeam =  teamCreator.createChaosTeam();
        PlayerDefinition line = playerCreator.createWardancer();
        ResultMap<Void> hiring = rcTeam.hirePlayer(line, messageSource);
        Assertions.assertTrue(hiring.isFailure());
        Assertions.assertEquals("team.player:Le roster 01KCCH67VJYCMKEN0R43F8KVWD/Chaos Chosen ne contient pas de joueur \"01KCSHX7EPNSF162TQE59BHW3P/Wardancer\", impossible de recruter ce type de joueur.", hiring.getError().strip());
    }

    @Test
    @DisplayName("Hire player should fail if player definition is not in chosen roster")
    void hireBadPlayerShouldFails() {
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterSelectedTeam rcTeam =  teamCreator.createChaosTeam(chaosPact);

        Roster woodies = rosterCreator.createWoodElves();
        PlayerDefinition warDancer = woodies.getPlayerDefinitions().getFirst();
        ResultMap<Void> hiring = rcTeam.hirePlayer(warDancer, messageSource);

        Assertions.assertTrue(hiring.isFailure());
        Assertions.assertEquals("team.player:Le roster 01KCSGB4E6XWNQNVAQN3PSXKGB/Chaos Pact ne contient pas de joueur \"01KCSHX7EPNSF162TQE59BHW3P/Wardancer\", impossible de recruter ce type de joueur.", hiring.getError().strip());
    }

    @Test
    @DisplayName("Hire player should fail if 16 players are already hired in team")
    void hiringShouldFailIfPlayerIs16() {
        RosterSelectedTeam with16Players = teamCreator.createRosterTeamWith16Player();
        PlayerDefinition warDancer = playerCreator.createWardancer();

        ResultMap<Void> hiring = with16Players.hirePlayer(warDancer, messageSource);
        Assertions.assertTrue(hiring.isFailure());
        Assertions.assertEquals("team.player:L'équipe 01KCSJRWAFMN3T35AVZDX1ASXP/teamName a déjà atteint son maximum de joueurs", hiring.getError());
    }

    @Test
    @DisplayName("Hire player should fail if team is invalid")
    void hiringShouldFailIfTeamIsInvalid() {
        RosterSelectedTeam with16Players = teamCreator.createRosterTeamWith16Player();
        PlayerDefinition warDancer = playerCreator.createWardancer();

        ResultMap<Void> hiring = with16Players.hirePlayer(warDancer, messageSource);
        Assertions.assertTrue(hiring.isFailure());
        Assertions.assertEquals("team.player:L'équipe 01KCSJRWAFMN3T35AVZDX1ASXP/teamName a déjà atteint son maximum de joueurs", hiring.getError());
    }

    @Test
    @DisplayName("Hire many player should succed")
    void hireManyPlayerShoudSucceed() {
        Roster darkies = rosterCreator.createDarkElves();
        RosterSelectedTeam teamWithDarkies = teamCreator.createChaosTeam(darkies);
        PlayerDefinition witches = playerCreator.createWitchElf();
        PlayerDefinition blitzer = playerCreator.createBlitzer();
        PlayerDefinition assassin = playerCreator.createAssassin();
        PlayerDefinition linemens = playerCreator.createLineman();
        ResultMap<Void> hiring = teamWithDarkies.hireManyPlayers(List.of(witches, witches, blitzer, blitzer, assassin, linemens, linemens, linemens, linemens), messageSource);
        Assertions.assertTrue(hiring.isSuccess());
        Assertions.assertEquals(teamWithDarkies.getHiredPlayerCount(), 9);
    }

    @Test
    @DisplayName("Hire player should fail if max of player of type is already reached")
    void hiringShouldFailMaxOfTypeAlreadyHired() {
        Roster darkies = rosterCreator.createDarkElves();
        RosterSelectedTeam teamWithDarkies = teamCreator.createChaosTeam(darkies);
        PlayerDefinition witches = playerCreator.createWitchElf();
        teamWithDarkies.hireManyPlayers(List.of(witches, witches), messageSource);
        ResultMap<Void> hiring = teamWithDarkies.hirePlayer(witches, messageSource);
        Assertions.assertTrue(hiring.isFailure());
        Assertions.assertEquals(2, teamWithDarkies.getHiredPlayerCount());
        Assertions.assertEquals("team.player:L'équipe 01KCSHJS1K5M8JTW9D5A58VY1S/teamName a déjà atteint son maximum de positionnel de type 01KCVWCDHJVZAW5XC1TZ96QDTZ/Witch Elf", hiring.getError());
    }

    @Test
    @DisplayName("Hire player should fail if player of type 'one in' already hired")
    void hiringOfMoreOneOfTypeShouldFail() {
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterSelectedTeam teamWithChaosPact = teamCreator.createChaosTeam(chaosPact);
        PlayerDefinition minotaur = playerCreator.createMinotaur();
        PlayerDefinition troll = playerCreator.createTroll();
        PlayerDefinition ratOgre = playerCreator.createRatOgre();
        PlayerDefinition ogre = playerCreator.createOgre();
        ResultMap<Void> minoHiring = teamWithChaosPact.hireManyPlayers(List.of(minotaur, ogre, troll), messageSource);
        Assertions.assertTrue(minoHiring.isSuccess());

        ResultMap<Void> ogreHiring = teamWithChaosPact.hirePlayer(ratOgre, messageSource);
        Assertions.assertTrue(ogreHiring.isFailure());
        Assertions.assertEquals("team.player:L'equipe 01KCSHJS1K5M8JTW9D5A58VY1S/teamName a déjà atteint son maximum de joueurs en limites croisées", ogreHiring.getError());
    }

    @Test
    @DisplayName("Hire player should fail if there is not enough remaining budget")
    void hiringShouldFailIfRemainingBudgetIsNotEnough() {
        Roster darkElfs = rosterCreator.createDarkElves();
        Ruleset ruleset = rulesetCreator.createRulesetWithTwoTiers();
        RosterSelectedTeam teamOfDarkElfs = teamCreator.createTeam(darkElfs, ruleset);
        PlayerDefinition witch = playerCreator.createWitchElf();
        PlayerDefinition blitzer = playerCreator.createBlitzer();
        PlayerDefinition lineman = playerCreator.createLineman();
        PlayerDefinition assassin = playerCreator.createAssassin();

        ResultMap<Void> playerHiring = teamOfDarkElfs.hireManyPlayers(List.of(witch, witch, assassin, assassin, blitzer, blitzer, lineman, lineman, lineman,lineman,lineman, lineman, lineman), messageSource);
        Assertions.assertTrue(playerHiring.isFailure());
        Assertions.assertEquals("team.player:la team 01KCSHJS1K5M8JTW9D5A58VY1S/teamName ne dispose pas d'un budget suffisant pour recruter le joueur 01KCVWJTB9J6D7NJNSPS81N296/Dark elf lineman", playerHiring.getError());
    }

    @Test
    @DisplayName("Hire 5 players, shall record 5 events")
    void testHireFivePlayersShallRecordFiveEvents() {
        Roster darkElfs = rosterCreator.createDarkElves();
        Ruleset ruleset = rulesetCreator.createRulesetWithTwoTiers();
        RosterSelectedTeam teamOfDarkElfs = teamCreator.createTeam(darkElfs, ruleset);
        PlayerDefinition witch = playerCreator.createWitchElf();
        PlayerDefinition blitzer = playerCreator.createBlitzer();
        PlayerDefinition assassin = playerCreator.createAssassin();

        ResultMap<Void> playerHiring = teamOfDarkElfs.hireManyPlayers(List.of(witch, witch, assassin, assassin, blitzer), messageSource);
        Assertions.assertTrue(playerHiring.isSuccess());
        Assertions.assertEquals(5, teamOfDarkElfs.domainEvents().size());
        assertEqualsResultset(teamOfDarkElfs);
    }

    @Test
    @DisplayName("change roster shall reset player list")
    void ChangeRosterShallResetPlayerList() {
        Roster darkElfs = rosterCreator.createDarkElves();
        Ruleset ruleset = rulesetCreator.createRulesetWithTwoTiers();
        RosterSelectedTeam teamOfDarkElfs = teamCreator.createTeam(darkElfs, ruleset);
        PlayerDefinition lineman = playerCreator.createLineman();

        ResultMap<Void> playerHiring = teamOfDarkElfs.hireManyPlayers(List.of(lineman, lineman, lineman), messageSource);
        Assertions.assertTrue(playerHiring.isSuccess());
        Assertions.assertEquals(3, teamOfDarkElfs.domainEvents().size());

        // change with same roster, doesn't do anything
        ResultMap<Void> sameRosterChanging = teamOfDarkElfs.chooseRoster(darkElfs, messageSource);
        Assertions.assertTrue(sameRosterChanging.isSuccess());
        Assertions.assertEquals(3, teamOfDarkElfs.domainEvents().size());

        // change with another roster shall record an event and reset players hired list
        Roster proElves = rosterCreator.createProElves();
        ResultMap<Void>  anotherRosterSelecting = teamOfDarkElfs.chooseRoster(proElves, messageSource);
        Assertions.assertTrue(anotherRosterSelecting.isSuccess());
        Assertions.assertEquals(4, teamOfDarkElfs.domainEvents().size());
        Assertions.assertEquals(0, teamOfDarkElfs.getHiredPlayers().size());
    }

    @Test
    @DisplayName("Remove player should success")
    void RemovePlayerFromTeamShouldSuccess() {
        Roster darkElfs = rosterCreator.createDarkElves();
        Ruleset ruleset = rulesetCreator.createRulesetWithTwoTiers();
        RosterSelectedTeam teamOfDarkElfs = teamCreator.createTeam(darkElfs, ruleset);
        PlayerDefinition lineman = playerCreator.createLineman();

        ResultMap<Void> playerHiring = teamOfDarkElfs.hireManyPlayers(List.of(lineman, lineman, lineman), messageSource);
        Assertions.assertTrue(playerHiring.isSuccess());

        ResultMap<Void> playerFiring = teamOfDarkElfs.removePlayer(lineman, messageSource);
        Assertions.assertTrue(playerFiring.isSuccess());
        Assertions.assertEquals(2,teamOfDarkElfs.getHiredPlayers().size());
        Assertions.assertEquals(teamOfDarkElfs.domainEvents().size(), 4);
        Assertions.assertEquals(PlayerRemovedEvent.class, teamOfDarkElfs.domainEvents().getLast().getClass());
    }

    @Test
    @DisplayName("Remove not hired player should fail")
    void RmoveNotHiredPlayerShouldFail() {
        Roster darkElfs = rosterCreator.createDarkElves();
        Ruleset ruleset = rulesetCreator.createRulesetWithTwoTiers();
        RosterSelectedTeam teamOfDarkElfs = teamCreator.createTeam(darkElfs, ruleset);
        PlayerDefinition lineman = playerCreator.createLineman();
        PlayerDefinition witch = playerCreator.createWitchElf();

        teamOfDarkElfs.hireManyPlayers(List.of(lineman, lineman, lineman), messageSource);
        ResultMap<Void> playerFiring = teamOfDarkElfs.removePlayer(witch, messageSource);
        Assertions.assertTrue(playerFiring.isFailure());
        Assertions.assertEquals("team:Le joueur 01KCVWCDHJVZAW5XC1TZ96QDTZ/Witch Elf n'a pas été recruté, impossible de le supprimer", playerFiring.getError());
    }

    @Test
    @DisplayName("change roster shall reset reroll count")
    void testChangeRosterShouldResetRerollList() {
        //Given
        Roster darkElfs = rosterCreator.createDarkElves();
        Ruleset ruleset = rulesetCreator.createRulesetWithTwoTiers();
        RosterSelectedTeam teamOfDarkElfs = teamCreator.createTeam(darkElfs, ruleset);
        PlayerDefinition lineman = playerCreator.createLineman();
        ResultMap<Void> playerHiring = teamOfDarkElfs.hireManyPlayers(List.of(lineman, lineman, lineman), messageSource);
        ResultMap<Void> rerollPruchase = teamOfDarkElfs.purchaseReroll(3, messageSource);
        Assertions.assertTrue(rerollPruchase.isSuccess());
        Assertions.assertEquals(3, teamOfDarkElfs.getRerollCount());

        // when
        Roster proElves = rosterCreator.createProElves();
        ResultMap<Void>  anotherRosterSelecting = teamOfDarkElfs.chooseRoster(proElves, messageSource);
        Assertions.assertTrue(anotherRosterSelecting.isSuccess());

        // should contain 3 hireplayer, 1 reroll purchase, 1 roster change
        Assertions.assertEquals(5, teamOfDarkElfs.domainEvents().size());
        Assertions.assertEquals(0, teamOfDarkElfs.getHiredPlayers().size());
        Assertions.assertEquals(0, teamOfDarkElfs.getRerollCount());
    }

    @Test
    @DisplayName("change roster shall reset team staff")
    void testChangeRosterShouldResetTeamStaff() {
        // Given
        Roster darkElfs = rosterCreator.createDarkElves();
        Ruleset ruleset = rulesetCreator.createRulesetWithTwoTiers();
        RosterSelectedTeam teamOfDarkElfs = teamCreator.createTeam(darkElfs, ruleset);

        ResultMap<Void> staffBuying = teamOfDarkElfs.buyStaff(staffCreator.createCheerleaders(), messageSource);
        Assertions.assertTrue(staffBuying.isSuccess());
        Assertions.assertEquals(1, teamOfDarkElfs.getCheerleaders());
        Assertions.assertEquals(10, teamOfDarkElfs.getStaffBudget());

        // change with same roster, doesn't do anything
        ResultMap<Void> sameRosterChanging = teamOfDarkElfs.chooseRoster(darkElfs, messageSource);
        Assertions.assertTrue(sameRosterChanging.isSuccess());
        Assertions.assertEquals(1, teamOfDarkElfs.getCheerleaders());

        // when - change with another roster shall reset team staff
        Roster proElves = rosterCreator.createProElves();
        ResultMap<Void> anotherRosterSelecting = teamOfDarkElfs.chooseRoster(proElves, messageSource);
        Assertions.assertTrue(anotherRosterSelecting.isSuccess());

        // then
        Assertions.assertEquals(0, teamOfDarkElfs.getCheerleaders());
        Assertions.assertEquals(0, teamOfDarkElfs.getStaffBudget());
    }

}
