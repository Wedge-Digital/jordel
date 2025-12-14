package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.team_building.domain.events.RosterChosenEvent;
import com.bloodbowlclub.test_utilities.AssertLib;
import com.bloodbowlclub.test_utilities.team_creation.RosterCreator;
import com.bloodbowlclub.test_utilities.team_creation.TeamCreationRulesetCreator;
import com.bloodbowlclub.test_utilities.team_creation.TeamCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class ChooseRosterTest extends TestCase {

    TeamCreationRulesetCreator creator = new TeamCreationRulesetCreator();

    @Test
    @DisplayName("")
    void testChooseRosterSucceed() {
        Roster chaos = RosterCreator.createRoster();
        CreationRulesetChosenTeam team = TeamCreator.createRulesetChosenTeam();
        AssertLib.AssertHasNoDomainEvent(team);
        ResultMap<Void> rosterChoice = team.chooseRoster(chaos, null);
        Assertions.assertTrue(rosterChoice.isSuccess());
        AssertLib.AssertHasDomainEventOfType(team, RosterChosenEvent.class);
    }

    @Test
    @DisplayName("change roster shall be possible")
    void testChangeRosterSucceed() {
        RosterChosenTeam rcTeam = TeamCreator.createRosterChosenTeam();
        AssertLib.AssertHasNoDomainEvent(rcTeam);
        Roster anotherRoster = RosterCreator.createRoster("AnotherRoster");
        ResultMap<Void> rosterChoice = rcTeam.chooseRoster(anotherRoster, null);
        Assertions.assertTrue(rosterChoice.isSuccess());
        AssertLib.AssertHasDomainEventOfType(rcTeam, RosterChosenEvent.class);
    }

    @Test
    @DisplayName("Hydratation of a team with changed roster shall be ok")
    void testChangeRosterHydratationShallBeOk() {
        Roster anotherRoster = RosterCreator.createRoster("anotherRoster");

        RosterChosenTeam team = TeamCreator.createRosterChosenTeam();

        RosterChosenEvent rcEvent2 = new RosterChosenEvent(team, anotherRoster);

        RosterChosenTeam hydrated = (RosterChosenTeam) team.apply(rcEvent2).getValue();
        Assertions.assertTrue(hydrated.getRoster().getRosterName().equalsString("anotherRoster"));
        assertEqualsResultset(hydrated);
    }

    @Test
    @DisplayName("Roster selection shall not be possible, if roster is not available in creation ruleset")
    void checkRosterIsPresentInRuleset() {
        TeamCreationRuleset ruleset = creator.builder("My Ruleset").withDarkies().build();

        Roster woodElves = RosterCreator.createWoodElves();
        CreationRulesetChosenTeam team = TeamCreator.createRulesetChosenTeam(ruleset);
        ResultMap<Void> rosterSelection = team.chooseRoster(woodElves, messageSource);
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

        Roster darkies = RosterCreator.createDarkElves();
        ResultMap<Void> rosterSelection = team.chooseRoster(darkies, messageSource);
        Assertions.assertTrue(rosterSelection.isSuccess());
        AssertLib.AssertHasDomainEventOfType(team, RosterChosenEvent.class);
    }


}
