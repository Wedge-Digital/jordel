package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.team_building.domain.TeamCreationRuleset;
import com.bloodbowlclub.team_building.domain.TeamCreationRulesetID;

public class TeamCreationRulesetCreator {

    public static TeamCreationRuleset createTeamCreationRulset() {
        return TeamCreationRuleset.builder()
                .rulesetID(new TeamCreationRulesetID("01KCE8V76ZB6Y1EP0N2N4X1W90"))
                .build();
    }

    public static TeamCreationRuleset createBadTeamCreationRulset() {
        return TeamCreationRuleset.builder()
                .rulesetID(new TeamCreationRulesetID("baaaaad_rule"))
                .build();
    }

}
