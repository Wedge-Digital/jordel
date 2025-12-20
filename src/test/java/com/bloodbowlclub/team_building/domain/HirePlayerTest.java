package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.test_utilities.team_creation.PlayerDefinitionCreator;
import com.bloodbowlclub.test_utilities.team_creation.RosterCreator;
import com.bloodbowlclub.test_utilities.team_creation.TeamCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class HirePlayerTest extends TestCase {

    TeamCreator teamCreator = new TeamCreator();
    RosterCreator rosterCreator = new RosterCreator();
    PlayerDefinitionCreator playerCreator = new PlayerDefinitionCreator();

    @Test
    @DisplayName("hiring a player should be ok, if no player are previously drafted, and player definition exist in roster")
    void testHirePlayerOk() {
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterChosenTeam rcTeam =  teamCreator.createRosterChosenTeam(chaosPact);
        PlayerDefinition toHire = chaosPact.getPlayerDefinitions().getFirst();
        ResultMap<Void> hiring = rcTeam.hirePlayer(toHire, messageSource);
        Assertions.assertTrue(hiring.isSuccess());
    }

    @Test
    @DisplayName("hiring a player should be failing, if no roster lines exist in roster")
    void testHirePlayerWithEmptyRosterFails() {
        RosterChosenTeam rcTeam =  TeamCreator.createRosterChosenTeam();
        PlayerDefinition line = playerCreator.createWardancer();
        ResultMap<Void> hiring = rcTeam.hirePlayer(line, messageSource);
        Assertions.assertTrue(hiring.isFailure());
        Assertions.assertEquals("team:Le roster Chaos Chosen ne contient pas de joueur \"Wardancer\", impossible de recruter ce type de joueur.", hiring.getError().strip());
    }

    @Test
    @DisplayName("Hire player should fail if player definition is not in chosen roster")
    void hireBadPlayerShouldFails() {
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterChosenTeam rcTeam =  teamCreator.createRosterChosenTeam(chaosPact);

        Roster woodies = rosterCreator.createWoodElves();
        PlayerDefinition warDancer = woodies.getPlayerDefinitions().getFirst();
        ResultMap<Void> hiring = rcTeam.hirePlayer(warDancer, messageSource);

        Assertions.assertTrue(hiring.isFailure());
        Assertions.assertEquals("team:Le roster Chaos Pact ne contient pas de joueur \"Wardancer\", impossible de recruter ce type de joueur.", hiring.getError().strip());
    }

    @Test
    @DisplayName("Hire many player should succeed")
    void hireManyPlayersSucceed() {

    }

    @Test
    @DisplayName("Hire player should fail if 16 players are already hired in team")
    void hiringShouldFailIfPlayerIs16() {
        RosterChosenTeam with16Players = teamCreator.createRosterTeamWith16Player();
        PlayerDefinition warDancer = playerCreator.createWardancer();

        ResultMap<Void> hiring = with16Players.hirePlayer(warDancer, messageSource);
        Assertions.assertTrue(hiring.isFailure());
        Assertions.assertEquals("team:L'équipe teamName a déjà atteint son maximum de joueurs", hiring.getError());
    }

    @Test
    @DisplayName("Hire many player should succed")
    void hireManyPlayerShoudSucceed() {
        Roster darkies = rosterCreator.createDarkElves();
        RosterChosenTeam teamWithDarkies = teamCreator.createRosterChosenTeam(darkies);
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
        RosterChosenTeam teamWithDarkies = teamCreator.createRosterChosenTeam(darkies);
        PlayerDefinition witches = playerCreator.createWitchElf();
        teamWithDarkies.hireManyPlayers(List.of(witches, witches), messageSource);
        ResultMap<Void> hiring = teamWithDarkies.hirePlayer(witches, messageSource);
        Assertions.assertTrue(hiring.isFailure());
        Assertions.assertEquals(2, teamWithDarkies.getHiredPlayerCount());
        Assertions.assertEquals("team:L'équipe teamName a déjà atteint son maximum de positionnel de type Witch Elf", hiring.getError());
    }

    @Test
    @DisplayName("Hire player should fail if player of type 'one in' already hired")
    void hiringOfMoreOneOfTypeShouldFail() {
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterChosenTeam teamWithChaosPact = teamCreator.createRosterChosenTeam(chaosPact);
        PlayerDefinition minotaur = playerCreator.createMinotaur();
        PlayerDefinition troll = playerCreator.createTroll();
        PlayerDefinition ratOgre = playerCreator.createRatOgre();
        PlayerDefinition ogre = playerCreator.createOgre();
        ResultMap<Void> minoHiring = teamWithChaosPact.hireManyPlayers(List.of(minotaur, ogre, troll), messageSource);
        Assertions.assertTrue(minoHiring.isSuccess());

        ResultMap<Void> ogreHiring = teamWithChaosPact.hirePlayer(ratOgre, messageSource);
        Assertions.assertTrue(ogreHiring.isFailure());
        Assertions.assertEquals("team:L'equipe teamName a déjà atteint son maximum de joueurs en limites croisées", ogreHiring.getError());
    }

    @Test
    @DisplayName("Hire player should fail if there is not enough remaining budget")
    void hiringShouldFailIfRemainingBudgetIsNotEnough() {
        Assertions.assertTrue(false);
    }


}
