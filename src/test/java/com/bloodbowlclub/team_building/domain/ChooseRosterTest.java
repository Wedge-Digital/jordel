package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.team_building.domain.events.CreationRulesetSelectedEvent;
import com.bloodbowlclub.team_building.domain.events.DraftTeamRegisteredEvent;
import com.bloodbowlclub.team_building.domain.events.RosterChosenEvent;
import com.bloodbowlclub.test_utilities.AssertLib;
import com.bloodbowlclub.test_utilities.team_creation.RosterCreator;
import com.bloodbowlclub.test_utilities.team_creation.RulesetCreator;
import com.bloodbowlclub.test_utilities.team_creation.TeamCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseRosterTest extends TestCase {

    RulesetCreator creator = new RulesetCreator();
    RosterCreator rosterCreator = new RosterCreator();
    Roster woodies = rosterCreator.createWoodElves();
    Roster darkies = rosterCreator.createDarkElves();
    TeamCreationRuleset fullRuleset = creator.builder().withWoodiesAndDarkies().build();
    CreationRulesetChosenTeam teamWithFullRuleset = TeamCreator.createRulesetChosenTeam(fullRuleset);

    @Test
    @DisplayName("")
    void testChooseRosterSucceed() {
        AssertLib.AssertHasNoDomainEvent(teamWithFullRuleset);
        ResultMap<Void> rosterChoice = teamWithFullRuleset.chooseRoster(woodies, messageSource);
        Assertions.assertTrue(rosterChoice.isSuccess());
        AssertLib.AssertHasDomainEventOfType(teamWithFullRuleset, RosterChosenEvent.class);
    }

    @Test
    @DisplayName("Change roster shall be possible")
    void testChangeRosterSucceed() {
        AssertLib.AssertHasNoDomainEvent(teamWithFullRuleset);
        teamWithFullRuleset.chooseRoster(woodies, messageSource);
        Assertions.assertEquals(1, teamWithFullRuleset.domainEvents().size());

        ResultMap<Void> rosterChange = teamWithFullRuleset.chooseRoster(darkies, messageSource);
        Assertions.assertTrue(rosterChange.isSuccess());
        Assertions.assertEquals(2, teamWithFullRuleset.domainEvents().size());
    }

    @Test
    @DisplayName("Hydratation of a team with changed roster shall be ok")
    void testChangeRosterHydratationShallBeOk() {

        DraftTeam team = TeamCreator.createDraftTeam();

        DraftTeamRegisteredEvent regEvent = new DraftTeamRegisteredEvent(team);
        CreationRulesetSelectedEvent rulesetEvent = new CreationRulesetSelectedEvent(team, fullRuleset);
        RosterChosenEvent rcEvent = new RosterChosenEvent(new CreationRulesetChosenTeam(team, fullRuleset), woodies);
        RosterChosenEvent rcEvent2 = new RosterChosenEvent(new CreationRulesetChosenTeam(team, fullRuleset), darkies);

        BaseTeam bt = TeamCreator.createBaseTeam();
        Result<AggregateRoot> hydratation = bt.hydrate(List.of(regEvent, rulesetEvent, rcEvent, rcEvent2));
        Assertions.assertTrue(hydratation.isSuccess());
        RosterChosenTeam hydrated = (RosterChosenTeam) hydratation.getValue();
        Assertions.assertTrue(hydrated.getRoster().getName().equalsString("Dark Elves"));
        assertEqualsResultset(hydrated);
    }

    @Test
    @DisplayName("Roster selection shall not be possible, if roster is not available in creation ruleset")
    void checkRosterIsPresentInRuleset() {
        TeamCreationRuleset ruleset = creator.builder("My Ruleset").withDarkies().build();

        CreationRulesetChosenTeam team = TeamCreator.createRulesetChosenTeam(ruleset);
        ResultMap<Void> rosterSelection = team.chooseRoster(woodies, messageSource);
        Assertions.assertTrue(rosterSelection.isFailure());
        Assertions.assertEquals(ErrorCode.INTERNAL_ERROR, rosterSelection.getErrorCode());
        Map<String, String> errors = rosterSelection.errorMap();
        HashMap<String,String> expectedErrors = new HashMap<>();
        expectedErrors.put("team", "Le roster Wood Elves n'est pas autorisé par les règles de création \"My Ruleset\", impossible de valider la sélection du roster.");
        Assertions.assertEquals(expectedErrors, errors);

    }

    @Test
    @DisplayName("Roster selection shall be possible, if roster is available in creation ruleset")
    void checkRosterIsPresentInRulesetHydratation() {
        TeamCreationRuleset ruleset = creator.builder().withDarkies().build();
        CreationRulesetChosenTeam team = TeamCreator.createRulesetChosenTeam(ruleset);

        Roster darkies = rosterCreator.createDarkElves();
        ResultMap<Void> rosterSelection = team.chooseRoster(darkies, messageSource);
        Assertions.assertTrue(rosterSelection.isSuccess());
        AssertLib.AssertHasDomainEventOfType(team, RosterChosenEvent.class);
    }

    @Test
    @DisplayName("Roster selection shall be ok, even with ruleset with two tier")
    void checkRosterSelectionIsOkWithTwoTierlist() {
        TeamCreationRuleset ruleset = creator.createRulesetWithTwoTiers();
        CreationRulesetChosenTeam team = TeamCreator.createRulesetChosenTeam(ruleset);

        Roster proElves = rosterCreator.createProElves();
        ResultMap<Void> rosterSelection = team.chooseRoster(proElves, messageSource);
        Assertions.assertTrue(rosterSelection.isSuccess());
        AssertLib.AssertHasDomainEventOfType(team, RosterChosenEvent.class);
        assertEqualsResultset(team);
    }


}
