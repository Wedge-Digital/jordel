package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.team_building.domain.*;

import java.util.List;

public class RulesetCreator {
    private static TeamCreationRuleset.TeamCreationRulesetBuilder<?, ?> current;
    private static final RosterCreator rosterCreator = new RosterCreator();

    private TierCreator tierCreator = new TierCreator();

    public TeamCreationRuleset createBasicRuleset() {
        Roster woodies = rosterCreator.createWoodElves();
        Roster darkies = rosterCreator.createDarkElves();
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterTier tier = tierCreator.createInfiniteBudgetTier(List.of(woodies, darkies, chaosPact));
        return TeamCreationRuleset.builder()
                .rulesetID(new TeamCreationRulesetID("01KCE8V76ZB6Y1EP0N2N4X1W90"))
                .name(new RulesetName("Basic Ruleset"))
                .tierList(List.of(tier))
                .build();
    }

    public static TeamCreationRuleset createBadTeamCreationRulset() {
        return TeamCreationRuleset.builder()
                .rulesetID(new TeamCreationRulesetID("baaaaad_rule"))
                .build();
    }

    public RulesetCreator builder() {

        current = TeamCreationRuleset.builder()
                .rulesetID(new TeamCreationRulesetID("01KCF1RZPXTHZWXXJTC1BPKB33"))
                .name(new RulesetName("Basic Ruleset"));
        return this;
    }

    public RulesetCreator builder(String ruleSetname) {

        current = TeamCreationRuleset.builder()
                .rulesetID(new TeamCreationRulesetID("01KCF1RZPXTHZWXXJTC1BPKB33"))
        .name(new RulesetName(ruleSetname));
        return this;
    }

    public RulesetCreator withWoodies() {
        Roster woodies = rosterCreator.createWoodElves();
        RosterTier topTier = TierCreator.createTier(List.of(woodies));
        current.tierList(List.of(topTier));
        return this;
    }

    public RulesetCreator withDarkies() {
        Roster darkies = rosterCreator.createDarkElves();
        RosterTier topTier = TierCreator.createTier(List.of(darkies));
        current.tierList(List.of(topTier));
        return this;
    }

    public RulesetCreator withWoodiesAndDarkies() {
        Roster darkies = rosterCreator.createDarkElves();
        Roster woodies = rosterCreator.createWoodElves();
        RosterTier topTier = TierCreator.createTier(List.of(woodies, darkies));
        current.tierList(List.of(topTier));
        return this;
    }

    public TeamCreationRuleset createRulesetWithTwoTiers() {
        Roster proElves = rosterCreator.createProElves();
        Roster woodies = rosterCreator.createWoodElves();
        Roster darkElfs = rosterCreator.createDarkElves();
        RosterTier topTier = tierCreator.createTopTier(List.of(woodies, darkElfs));
        RosterTier middleTier = tierCreator.createMiddleTier(List.of(proElves));

       return TeamCreationRuleset.builder()
                .rulesetID(new TeamCreationRulesetID("01KCY9AVNY783NJEV0TSSADSVR"))
                .name(new RulesetName("Euro Draft Ruleset"))
               .tierList(List.of(topTier, middleTier))
               .build();
    }

    public TeamCreationRuleset build() {
        return current.build();
    }
}
