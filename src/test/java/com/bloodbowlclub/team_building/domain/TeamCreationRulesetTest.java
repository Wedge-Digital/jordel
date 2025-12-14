package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.test_utilities.team_creation.RosterCreator;
import com.bloodbowlclub.test_utilities.team_creation.TeamCreationRulesetCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TeamCreationRulesetTest {

    TeamCreationRulesetCreator creator = new TeamCreationRulesetCreator();
    @Test
    @DisplayName("Check a roster in allowed roster, is allowed")
    void testAllowedRoster() {
        TeamCreationRuleset ruleset = creator.builder().withWoodies().build();
        Roster woodies = RosterCreator.createWoodElves();
        Roster darkElves = RosterCreator.createDarkElves();

        Assertions.assertTrue(ruleset.isRosterNotAllowed(darkElves));
        Assertions.assertTrue(ruleset.isRosterAllowed(woodies));
    }

}