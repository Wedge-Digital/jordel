package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.JsonService;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.shared.shared.cloudinary_url.CloudinaryUrl;
import com.bloodbowlclub.shared.team.TeamID;
import com.bloodbowlclub.shared.team.TeamName;
import com.bloodbowlclub.team_building.domain.events.*;
import com.bloodbowlclub.team_building.domain.roster.PlayerDefinition;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.bloodbowlclub.team_building.domain.ruleset.Ruleset;
import com.bloodbowlclub.team_building.domain.team.BaseTeam;
import com.bloodbowlclub.team_building.domain.team.DraftTeam;
import com.bloodbowlclub.team_building.domain.team.RosterSelectedTeam;
import com.bloodbowlclub.team_building.domain.team.RulesetSelectedTeam;
import com.bloodbowlclub.test_utilities.team_creation.PlayerDefinitionCreator;
import com.bloodbowlclub.test_utilities.team_creation.RosterCreator;
import com.bloodbowlclub.test_utilities.team_creation.RulesetCreator;
import com.bloodbowlclub.test_utilities.team_creation.TeamCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TeamCreationHydratation extends TestCase {

    TeamCreator teamCreator = new TeamCreator();
    RosterCreator rosterCreator = new RosterCreator();

    RulesetCreator rulesetCreator = new RulesetCreator();
    PlayerDefinitionCreator playerCreator = new PlayerDefinitionCreator();

    private DraftTeam hydrateToDraftTeam() {
        DraftTeam team = DraftTeam.builder()
                .teamId(new TeamID("01KCYVJSQS3CZ9R16ENT7XX20B"))
                .name(new TeamName("Bloody Beet Roots"))
                .logoUrl(new CloudinaryUrl("https://res.cloudinary.com/bloodbowlclub-com/image/upload/v1659445677/user_uploads/x44ke8sgvqrap91mry8i.jpg"))
                .build();
        DraftTeamRegisteredEvent creationEvent = new DraftTeamRegisteredEvent(team);
        BaseTeam base = new BaseTeam();
        Result<AggregateRoot> hydratation = base.hydrate(List.of(creationEvent));
        Assertions.assertTrue(hydratation.isSuccess());
        return (DraftTeam) hydratation.getValue();
    }

    private RulesetSelectedTeam hydrateToRulesetSelected(DraftTeam draftTeam, Ruleset ruleset) {
        RulesetSelectedEvent rulesetSelectedEvent = new RulesetSelectedEvent(draftTeam, ruleset);
        Result<AggregateRoot> hydratation = draftTeam.hydrate(List.of(rulesetSelectedEvent));
        Assertions.assertTrue(hydratation.isSuccess());
        return (RulesetSelectedTeam) hydratation.getValue();
    }

    private RosterSelectedTeam hydrateToRosterSelected(RulesetSelectedTeam team, Roster roster){
        RosterChosenEvent rosterChosenEvent = new RosterChosenEvent(team, roster);
        Result<AggregateRoot> hydratation = team.hydrate(List.of(rosterChosenEvent));
        Assertions.assertTrue(hydratation.isSuccess());
        return (RosterSelectedTeam) hydratation.getValue();
    }

    private RosterSelectedTeam hydratePlayerHiringHistory(RosterSelectedTeam team) {
        PlayerDefinition witch = playerCreator.createWitchElf();
        PlayerDefinition blitzer = playerCreator.createBlitzer();
        PlayerDefinition lineman = playerCreator.createLineman();
        PlayerDefinition assassin = playerCreator.createAssassin();

        PlayerHiredEvent witch1Hired = new PlayerHiredEvent(team, witch);
        PlayerHiredEvent witch2Hired = new PlayerHiredEvent(team, witch);
        PlayerHiredEvent blitzer1 = new PlayerHiredEvent(team, blitzer);
        PlayerHiredEvent blitzer2 = new PlayerHiredEvent(team, blitzer);
        PlayerHiredEvent lineman1 = new PlayerHiredEvent(team, lineman);
        PlayerHiredEvent lineman2 = new PlayerHiredEvent(team, lineman);
        PlayerHiredEvent lineman3 = new PlayerHiredEvent(team, lineman);
        PlayerHiredEvent assassin1 = new PlayerHiredEvent(team, assassin);

        Result<AggregateRoot> hydratation = team.hydrate(List.of(
                witch1Hired,
                witch2Hired,
                blitzer1,
                blitzer2,
                lineman1,
                lineman2,
                lineman3,
                assassin1
        ));
        Assertions.assertTrue(hydratation.isSuccess());
        return (RosterSelectedTeam) hydratation.getValue();
    }

    private RosterSelectedTeam hydrateToRosterReseted(RosterSelectedTeam team, Roster anotherRoster){
        RosterChosenEvent rosterChosenEvent = new RosterChosenEvent(team, anotherRoster);
        Result<AggregateRoot> hydratation = team.hydrate(List.of(rosterChosenEvent));
        Assertions.assertTrue(hydratation.isSuccess());
        return (RosterSelectedTeam) hydratation.getValue();
    }

    private RosterSelectedTeam hydrateToPlayerRemoval(RosterSelectedTeam team) {
        PlayerDefinition witch = playerCreator.createWitchElf();
        PlayerDefinition blitzer = playerCreator.createBlitzer();
        PlayerRemovedEvent  witchRemoved = new PlayerRemovedEvent(team, witch);
        PlayerRemovedEvent  blitzerRemoved = new PlayerRemovedEvent(team, blitzer);
        PlayerRemovedEvent  blitzer2Removed = new PlayerRemovedEvent(team, blitzer);
        Result<AggregateRoot> hydratation = team.hydrate(List.of(witchRemoved, blitzerRemoved, blitzer2Removed));
        Assertions.assertTrue(hydratation.isSuccess());
        return (RosterSelectedTeam) hydratation.getValue();
    }

    private RosterSelectedTeam hydrateFromFullHistory(Ruleset ruleset, Roster roster) {
        DraftTeam team = hydrateToDraftTeam();
        RulesetSelectedTeam rulesetSelectedTeam = hydrateToRulesetSelected(team, ruleset);
        RosterSelectedTeam rosterSelectedTeam = hydrateToRosterSelected(rulesetSelectedTeam, roster);
        return hydratePlayerHiringHistory(rosterSelectedTeam);
    }

    private RosterSelectedTeam hydrateFromFullHistoryWithRosterChange(Ruleset ruleset, Roster initialRoster, Roster anotherRoster) {
        DraftTeam team = hydrateToDraftTeam();
        RulesetSelectedTeam rulesetSelectedTeam = hydrateToRulesetSelected(team, ruleset);
        RosterSelectedTeam rosterSelectedTeam = hydrateToRosterSelected(rulesetSelectedTeam, initialRoster);
        RosterSelectedTeam teamWithPlayers = hydratePlayerHiringHistory(rosterSelectedTeam);
        return hydrateToRosterReseted(teamWithPlayers, anotherRoster);
    }

    private RosterSelectedTeam hydrateFromFullHistoryWithPlayerRemoval(Ruleset ruleset, Roster initialRoster) {
        DraftTeam team = hydrateToDraftTeam();
        RulesetSelectedTeam rulesetSelectedTeam = hydrateToRulesetSelected(team, ruleset);
        RosterSelectedTeam rosterSelectedTeam = hydrateToRosterSelected(rulesetSelectedTeam, initialRoster);
        RosterSelectedTeam teamWithPlayers = hydratePlayerHiringHistory(rosterSelectedTeam);
        return hydrateToPlayerRemoval(teamWithPlayers);
    }

    private RosterSelectedTeam buildReferenceTeam(Ruleset ruleset, Roster roster ) {
        DraftTeam team = DraftTeam.builder()
                .teamId(new TeamID("01KCYVJSQS3CZ9R16ENT7XX20B"))
                .name(new TeamName("Bloody Beet Roots"))
                .logoUrl(new CloudinaryUrl("https://res.cloudinary.com/bloodbowlclub-com/image/upload/v1659445677/user_uploads/x44ke8sgvqrap91mry8i.jpg"))
                .build();
        RulesetSelectedTeam rulesetChosenTeam = new RulesetSelectedTeam(team, ruleset);
        RosterSelectedTeam rosterChosenTeam = new RosterSelectedTeam(rulesetChosenTeam, roster);
        PlayerDefinition witch = playerCreator.createWitchElf();
        PlayerDefinition blitzer = playerCreator.createBlitzer();
        PlayerDefinition lineman = playerCreator.createLineman();
        PlayerDefinition assassin = playerCreator.createAssassin();
        rosterChosenTeam.hireManyPlayers(List.of(witch,witch,blitzer,blitzer,lineman,lineman,lineman,assassin), messageSource);
        return rosterChosenTeam;
    }

    private RosterSelectedTeam buildRefTeamWithRemoval(Ruleset ruleset, Roster roster) {
        DraftTeam team = DraftTeam.builder()
                .teamId(new TeamID("01KCYVJSQS3CZ9R16ENT7XX20B"))
                .name(new TeamName("Bloody Beet Roots"))
                .logoUrl(new CloudinaryUrl("https://res.cloudinary.com/bloodbowlclub-com/image/upload/v1659445677/user_uploads/x44ke8sgvqrap91mry8i.jpg"))
                .build();
        RulesetSelectedTeam rulesetChosenTeam = new RulesetSelectedTeam(team, ruleset);
        RosterSelectedTeam rosterChosenTeam = new RosterSelectedTeam(rulesetChosenTeam, roster);
        PlayerDefinition witch = playerCreator.createWitchElf();
        PlayerDefinition blitzer = playerCreator.createBlitzer();
        PlayerDefinition lineman = playerCreator.createLineman();
        PlayerDefinition assassin = playerCreator.createAssassin();
        rosterChosenTeam.hireManyPlayers(List.of(witch,witch,blitzer,blitzer,lineman,lineman,lineman,assassin), messageSource);
        rosterChosenTeam.removePlayer(witch, messageSource);
        rosterChosenTeam.removePlayer(blitzer, messageSource);
        rosterChosenTeam.removePlayer(blitzer, messageSource);
        return rosterChosenTeam;
    }

    @Test
    @DisplayName("hydrate team from full history, succeed")
    void testHydrateTeamFromFullHistorySucceed() {
        // Given
        Ruleset ruleset = rulesetCreator.createRulesetWithTwoTiers();
        Roster roster = rosterCreator.createDarkElves();

        // when
        RosterSelectedTeam referenceTeam = buildReferenceTeam(ruleset, roster);
        RosterSelectedTeam hydratedTeam = hydrateFromFullHistory(ruleset, roster);

        // then
        JsonService jsonService = new JsonService();
        String refTeam = jsonService.asJsonString(referenceTeam);
        String hydratedString = jsonService.asJsonString(hydratedTeam);
        Assertions.assertEquals(refTeam, hydratedString);
        assertEqualsResultset(hydratedTeam);
    }

    @Test
    @DisplayName("hydrate team from full history, including roster change, succeed")
    void testHydrateIncludingRosterChange() {
        // Given
        Ruleset ruleset = rulesetCreator.createRulesetWithTwoTiers();
        Roster darkies = rosterCreator.createDarkElves();
        Roster woodies = rosterCreator.createWoodElves();

        // when
        RosterSelectedTeam referenceTeam = buildReferenceTeam(ruleset, darkies);
        referenceTeam.chooseRoster(woodies, messageSource);
        Assertions.assertEquals(referenceTeam.getRoster(), woodies);

        RosterSelectedTeam hydratedTeam = hydrateFromFullHistoryWithRosterChange(ruleset, darkies, woodies);

        // then
        Assertions.assertEquals(hydratedTeam.getRoster(), woodies);
        JsonService jsonService = new JsonService();
        String refTeam = jsonService.asJsonString(referenceTeam);
        String hydratedString = jsonService.asJsonString(hydratedTeam);
        Assertions.assertEquals(hydratedTeam.getRoster(), woodies);
        Assertions.assertEquals(refTeam, hydratedString);
        assertEqualsResultset(hydratedTeam);
    }

    @Test
    @DisplayName("hydrate team from full history, including player removal, succeed")
    void testHydrateWithPlayerRemoval() {
        // Given
        Ruleset ruleset = rulesetCreator.createRulesetWithTwoTiers();
        Roster darkies = rosterCreator.createDarkElves();

        // when
        RosterSelectedTeam referenceTeam = buildRefTeamWithRemoval(ruleset, darkies);
        Assertions.assertEquals(referenceTeam.getHiredPlayerCount(), 5);

        RosterSelectedTeam hydratedTeam = hydrateFromFullHistoryWithPlayerRemoval(ruleset, darkies);

        // then
        Assertions.assertEquals(hydratedTeam.getHiredPlayerCount(), 5);
        JsonService jsonService = new JsonService();
        String refTeam = jsonService.asJsonString(referenceTeam);
        String hydratedString = jsonService.asJsonString(hydratedTeam);
        Assertions.assertEquals(refTeam, hydratedString);
        assertEqualsResultset(hydratedTeam);
    }

}
