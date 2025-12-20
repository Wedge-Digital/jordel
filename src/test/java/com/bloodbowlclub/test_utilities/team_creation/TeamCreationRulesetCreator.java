package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.team_building.domain.*;

import java.util.List;

public class TeamCreationRulesetCreator {
    private static TeamCreationRuleset.TeamCreationRulesetBuilder<?, ?> current;
    private static final RosterCreator rosterCreator = new RosterCreator();

    public static TeamCreationRuleset createBasicRulset() {
        return TeamCreationRuleset.builder()
                .rulesetID(new TeamCreationRulesetID("01KCE8V76ZB6Y1EP0N2N4X1W90"))
                .name(new RulesetName("Basic Ruleset"))
                .tierList(List.of())
                .build();
    }

    public static TeamCreationRuleset createBadTeamCreationRulset() {
        return TeamCreationRuleset.builder()
                .rulesetID(new TeamCreationRulesetID("baaaaad_rule"))
                .build();
    }

    public TeamCreationRulesetCreator builder() {

        current = TeamCreationRuleset.builder()
                .rulesetID(new TeamCreationRulesetID("01KCF1RZPXTHZWXXJTC1BPKB33"))
                .name(new RulesetName("Basic Ruleset"));
        return this;
    }

    public TeamCreationRulesetCreator builder(String ruleSetname) {

        current = TeamCreationRuleset.builder()
                .rulesetID(new TeamCreationRulesetID("01KCF1RZPXTHZWXXJTC1BPKB33"))
        .name(new RulesetName(ruleSetname));
        return this;
    }

    public TeamCreationRulesetCreator withWoodies() {
        Roster woodies = rosterCreator.createWoodElves();
        RosterTier topTier = TierCreator.createTier(List.of(woodies));
        current.tierList(List.of(topTier));
        return this;
    }

    public TeamCreationRulesetCreator withDarkies() {
        Roster darkies = rosterCreator.createDarkElves();
        RosterTier topTier = TierCreator.createTier(List.of(darkies));
        current.tierList(List.of(topTier));
        return this;
    }

    public TeamCreationRulesetCreator withWoodiesAndDarkies() {
        Roster darkies = rosterCreator.createDarkElves();
        Roster woodies = rosterCreator.createWoodElves();
        RosterTier topTier = TierCreator.createTier(List.of(woodies, darkies));
        current.tierList(List.of(topTier));
        return this;
    }

    public TeamCreationRulesetCreator withTwoTiers() {
        Roster proElves = rosterCreator.createProElves();
        Roster woodies = rosterCreator.createWoodElves();
        RosterTier topTier = TierCreator.createTier(List.of(woodies));
        RosterTier middleTier = TierCreator.createTier(List.of(proElves));
        current.tierList(List.of(topTier, middleTier));
        return this;
    }

    public TeamCreationRuleset build() {
        return current.build();
    }
}
