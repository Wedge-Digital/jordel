package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.JsonService;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.shared.shared.cloudinary_url.CloudinaryUrl;
import com.bloodbowlclub.shared.team.TeamID;
import com.bloodbowlclub.shared.team.TeamName;
import com.bloodbowlclub.team_building.domain.events.CreationRulesetSelectedEvent;
import com.bloodbowlclub.team_building.domain.events.DraftTeamRegisteredEvent;
import com.bloodbowlclub.team_building.domain.events.PlayerHiredEvent;
import com.bloodbowlclub.team_building.domain.events.RosterChosenEvent;
import com.bloodbowlclub.test_utilities.team_creation.PlayerDefinitionCreator;
import com.bloodbowlclub.test_utilities.team_creation.RosterCreator;
import com.bloodbowlclub.test_utilities.team_creation.RulesetCreator;
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

    @Test
    @DisplayName("hiring a player should be ok, if no player are previously drafted, and player definition exist in roster")
    void testHirePlayerOk() {
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterChosenTeam rcTeam =  teamCreator.createChaosTeam(chaosPact);
        PlayerDefinition toHire = chaosPact.getPlayerDefinitions().getFirst();
        ResultMap<Void> hiring = rcTeam.hirePlayer(toHire, messageSource);
        Assertions.assertTrue(hiring.isSuccess());
    }

    @Test
    @DisplayName("hiring a player should be failing, if this player definition doesn't exist in roster")
    void testHirePlayerWithEmptyRosterFails() {
        RosterChosenTeam rcTeam =  teamCreator.createChaosTeam();
        PlayerDefinition line = playerCreator.createWardancer();
        ResultMap<Void> hiring = rcTeam.hirePlayer(line, messageSource);
        Assertions.assertTrue(hiring.isFailure());
        Assertions.assertEquals("team:Le roster 01KCCH67VJYCMKEN0R43F8KVWD/Chaos Chosen ne contient pas de joueur \"01KCSHX7EPNSF162TQE59BHW3P/Wardancer\", impossible de recruter ce type de joueur.", hiring.getError().strip());
    }

    @Test
    @DisplayName("Hire player should fail if player definition is not in chosen roster")
    void hireBadPlayerShouldFails() {
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterChosenTeam rcTeam =  teamCreator.createChaosTeam(chaosPact);

        Roster woodies = rosterCreator.createWoodElves();
        PlayerDefinition warDancer = woodies.getPlayerDefinitions().getFirst();
        ResultMap<Void> hiring = rcTeam.hirePlayer(warDancer, messageSource);

        Assertions.assertTrue(hiring.isFailure());
        Assertions.assertEquals("team:Le roster 01KCSGB4E6XWNQNVAQN3PSXKGB/Chaos Pact ne contient pas de joueur \"01KCSHX7EPNSF162TQE59BHW3P/Wardancer\", impossible de recruter ce type de joueur.", hiring.getError().strip());
    }

    @Test
    @DisplayName("Hire player should fail if 16 players are already hired in team")
    void hiringShouldFailIfPlayerIs16() {
        RosterChosenTeam with16Players = teamCreator.createRosterTeamWith16Player();
        PlayerDefinition warDancer = playerCreator.createWardancer();

        ResultMap<Void> hiring = with16Players.hirePlayer(warDancer, messageSource);
        Assertions.assertTrue(hiring.isFailure());
        Assertions.assertEquals("team:L'équipe 01KCSJRWAFMN3T35AVZDX1ASXP/teamName a déjà atteint son maximum de joueurs", hiring.getError());
    }

    @Test
    @DisplayName("Hire player should fail if team is invalid")
    void hiringShouldFailIfTeamIsInvalid() {
        RosterChosenTeam with16Players = teamCreator.createRosterTeamWith16Player();
        PlayerDefinition warDancer = playerCreator.createWardancer();

        ResultMap<Void> hiring = with16Players.hirePlayer(warDancer, messageSource);
        Assertions.assertTrue(hiring.isFailure());
        Assertions.assertEquals("team:L'équipe 01KCSJRWAFMN3T35AVZDX1ASXP/teamName a déjà atteint son maximum de joueurs", hiring.getError());
    }

    @Test
    @DisplayName("Hire many player should succed")
    void hireManyPlayerShoudSucceed() {
        Roster darkies = rosterCreator.createDarkElves();
        RosterChosenTeam teamWithDarkies = teamCreator.createChaosTeam(darkies);
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
        RosterChosenTeam teamWithDarkies = teamCreator.createChaosTeam(darkies);
        PlayerDefinition witches = playerCreator.createWitchElf();
        teamWithDarkies.hireManyPlayers(List.of(witches, witches), messageSource);
        ResultMap<Void> hiring = teamWithDarkies.hirePlayer(witches, messageSource);
        Assertions.assertTrue(hiring.isFailure());
        Assertions.assertEquals(2, teamWithDarkies.getHiredPlayerCount());
        Assertions.assertEquals("team:L'équipe 01KCSHJS1K5M8JTW9D5A58VY1S/teamName a déjà atteint son maximum de positionnel de type 01KCVWCDHJVZAW5XC1TZ96QDTZ/Witch Elf", hiring.getError());
    }

    @Test
    @DisplayName("Hire player should fail if player of type 'one in' already hired")
    void hiringOfMoreOneOfTypeShouldFail() {
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterChosenTeam teamWithChaosPact = teamCreator.createChaosTeam(chaosPact);
        PlayerDefinition minotaur = playerCreator.createMinotaur();
        PlayerDefinition troll = playerCreator.createTroll();
        PlayerDefinition ratOgre = playerCreator.createRatOgre();
        PlayerDefinition ogre = playerCreator.createOgre();
        ResultMap<Void> minoHiring = teamWithChaosPact.hireManyPlayers(List.of(minotaur, ogre, troll), messageSource);
        Assertions.assertTrue(minoHiring.isSuccess());

        ResultMap<Void> ogreHiring = teamWithChaosPact.hirePlayer(ratOgre, messageSource);
        Assertions.assertTrue(ogreHiring.isFailure());
        Assertions.assertEquals("team:L'equipe 01KCSHJS1K5M8JTW9D5A58VY1S/teamName a déjà atteint son maximum de joueurs en limites croisées", ogreHiring.getError());
    }

    @Test
    @DisplayName("Hire player should fail if there is not enough remaining budget")
    void hiringShouldFailIfRemainingBudgetIsNotEnough() {
        Roster darkElfs = rosterCreator.createDarkElves();
        TeamCreationRuleset ruleset = rulesetCreator.createRulesetWithTwoTiers();
        RosterChosenTeam teamOfDarkElfs = teamCreator.createTeam(darkElfs, ruleset);
        PlayerDefinition witch = playerCreator.createWitchElf();
        PlayerDefinition blitzer = playerCreator.createBlitzer();
        PlayerDefinition lineman = playerCreator.createLineman();
        PlayerDefinition assassin = playerCreator.createAssassin();

        ResultMap<Void> playerHiring = teamOfDarkElfs.hireManyPlayers(List.of(witch, witch, assassin, assassin, blitzer, blitzer, lineman, lineman, lineman,lineman,lineman, lineman, lineman), messageSource);
        Assertions.assertTrue(playerHiring.isFailure());
        Assertions.assertEquals("team:la team 01KCSHJS1K5M8JTW9D5A58VY1S/teamName ne dispose pas d'un budget suffisant pour recruter le joueur 01KCVWJTB9J6D7NJNSPS81N296/Dark elf lineman", playerHiring.getError());
    }

    @Test
    @DisplayName("Hire 5 players, shall record 5 events")
    void testHireFivePlayersShallRecordFiveEvents() {
        Roster darkElfs = rosterCreator.createDarkElves();
        TeamCreationRuleset ruleset = rulesetCreator.createRulesetWithTwoTiers();
        RosterChosenTeam teamOfDarkElfs = teamCreator.createTeam(darkElfs, ruleset);
        PlayerDefinition witch = playerCreator.createWitchElf();
        PlayerDefinition blitzer = playerCreator.createBlitzer();
        PlayerDefinition assassin = playerCreator.createAssassin();

        ResultMap<Void> playerHiring = teamOfDarkElfs.hireManyPlayers(List.of(witch, witch, assassin, assassin, blitzer), messageSource);
        Assertions.assertTrue(playerHiring.isSuccess());
        Assertions.assertEquals(5, teamOfDarkElfs.domainEvents().size());
        assertEqualsResultset(teamOfDarkElfs);
    }

    @Test
    @DisplayName("hydrate team from full history, succeed")
    void testHydrateTeamFromFullHistorySucceed() {
        DraftTeam team = DraftTeam.builder()
                .teamId(new TeamID("01KCYVJSQS3CZ9R16ENT7XX20B"))
                .name(new TeamName("Bloody Beet Roots"))
                .logoUrl(new CloudinaryUrl("https://res.cloudinary.com/bloodbowlclub-com/image/upload/v1659445677/user_uploads/x44ke8sgvqrap91mry8i.jpg"))
                .build();


        TeamCreationRuleset ruleset = rulesetCreator.createRulesetWithTwoTiers();
        Roster roster = rosterCreator.createDarkElves();

        CreationRulesetChosenTeam rulesetChosenTeam = new  CreationRulesetChosenTeam(team, ruleset);
        RosterChosenTeam rosterChosenTeam = new RosterChosenTeam(rulesetChosenTeam, roster);
        PlayerDefinition witch = playerCreator.createWitchElf();
        PlayerDefinition blitzer = playerCreator.createBlitzer();
        PlayerDefinition lineman = playerCreator.createLineman();
        PlayerDefinition assassin = playerCreator.createAssassin();
        rosterChosenTeam.hireManyPlayers(List.of(witch,witch,blitzer,blitzer,lineman,lineman,lineman,assassin), messageSource);
        JsonService jsonService = new JsonService();
        String refTeam = jsonService.asJsonString(rosterChosenTeam);

        DraftTeamRegisteredEvent creationEvent = new DraftTeamRegisteredEvent(team);
        CreationRulesetSelectedEvent rulesetSelectedEvent = new CreationRulesetSelectedEvent(team, ruleset);
        RosterChosenEvent rosterChosenEvent = new RosterChosenEvent(rulesetChosenTeam, roster);
        PlayerHiredEvent witch1Hired = new PlayerHiredEvent(rosterChosenTeam, witch);
        PlayerHiredEvent witch2Hired = new PlayerHiredEvent(rosterChosenTeam, witch);
        PlayerHiredEvent blitzer1 = new PlayerHiredEvent(rosterChosenTeam, blitzer);
        PlayerHiredEvent blitzer2 = new PlayerHiredEvent(rosterChosenTeam, blitzer);
        PlayerHiredEvent lineman1 = new PlayerHiredEvent(rosterChosenTeam, lineman);
        PlayerHiredEvent lineman2 = new PlayerHiredEvent(rosterChosenTeam, lineman);
        PlayerHiredEvent lineman3 = new PlayerHiredEvent(rosterChosenTeam, lineman);
        PlayerHiredEvent assassin1 = new PlayerHiredEvent(rosterChosenTeam, assassin);
        BaseTeam base = new BaseTeam();
        Result<AggregateRoot> hydrated = base.hydrate(List.of(
                creationEvent,
                rulesetSelectedEvent,
                rosterChosenEvent,
                witch1Hired,
                witch2Hired,
                blitzer1,
                blitzer2,
                lineman1,
                lineman2,
                lineman3,
                assassin1
                ));

        Assertions.assertTrue(hydrated.isSuccess());
        String hydratedTeam = jsonService.asJsonString(hydrated.getValue());
        Assertions.assertEquals(refTeam, hydratedTeam);
        assertEqualsResultset(hydrated.getValue());
    }

}
