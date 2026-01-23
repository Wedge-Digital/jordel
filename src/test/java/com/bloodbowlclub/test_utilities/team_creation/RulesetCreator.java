package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.bloodbowlclub.team_building.domain.ruleset.RosterTier;
import com.bloodbowlclub.shared.ruleset.RulesetID;
import com.bloodbowlclub.shared.ruleset.RulesetName;
import com.bloodbowlclub.team_building.domain.ruleset.Ruleset;

import java.util.List;

public class RulesetCreator {
    private static final RosterCreator rosterCreator = new RosterCreator();

    private TierCreator tierCreator = new TierCreator();

    public Ruleset createBasicRuleset() {
        Roster woodies = rosterCreator.createWoodElves();
        Roster darkies = rosterCreator.createDarkElves();
        Roster chaosPact = rosterCreator.createChaosPact();
        Roster undead = rosterCreator.createUndead();
        Roster chaos = rosterCreator.createChaosChosen();
        RosterTier tier = tierCreator.createInfiniteBudgetTier(List.of(woodies, darkies, chaosPact, undead, chaos ));
        return Ruleset.builder()
                .rulesetID(new RulesetID("01KCE8V76ZB6Y1EP0N2N4X1W90"))
                .name(new RulesetName("Basic Ruleset"))
                .tierList(List.of(tier))
                .build();
    }

    public Ruleset createChoasPactRuleset() {
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterTier tier = tierCreator.createInfiniteBudgetTier(List.of(chaosPact));
        return Ruleset.builder()
                .rulesetID(new RulesetID("01KCYZ78YX2FHC3H3KD2Q0YE96"))
                .name(new RulesetName("Chaos Pact Ruleset"))
                .tierList(List.of(tier))
                .build();
    }

    public static Ruleset createBadTeamCreationRulset() {
        return Ruleset.builder()
                .rulesetID(new RulesetID("baaaaad_rule"))
                .build();
    }

    public Ruleset createRulesetWithTwoTiers() {
        Roster proElves = rosterCreator.createProElves();
        Roster woodies = rosterCreator.createWoodElves();
        Roster darkElfs = rosterCreator.createDarkElves();
        RosterTier topTier = tierCreator.createTopTier(List.of(woodies, darkElfs));
        RosterTier middleTier = tierCreator.createMiddleTier(List.of(proElves));

       return Ruleset.builder()
                .rulesetID(new RulesetID("01KCY9AVNY783NJEV0TSSADSVR"))
                .name(new RulesetName("Euro Draft Ruleset"))
               .tierList(List.of(topTier, middleTier))
               .build();
    }
}
