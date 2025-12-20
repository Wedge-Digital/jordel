package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.team_building.domain.events.CreationRulesetSelectedEvent;
import com.bloodbowlclub.team_building.domain.ruleset.Ruleset;
import com.bloodbowlclub.team_building.domain.team.CreationRulesetChosenTeam;
import com.bloodbowlclub.team_building.domain.team.DraftTeam;
import com.bloodbowlclub.test_utilities.AssertLib;
import com.bloodbowlclub.test_utilities.team_creation.RulesetCreator;
import com.bloodbowlclub.test_utilities.team_creation.TeamCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ChooseCreationRuleSetTest extends TestCase {
    RulesetCreator rulesetCreator = new RulesetCreator();
    @Test
    @DisplayName("a Draft Team shall be given valid team creation ruleSet, shall succeed")
    void testChooseCreationRuleSetOk() {
        DraftTeam team = TeamCreator.createDraftTeam();
        Ruleset ruleSet = rulesetCreator.createBasicRuleset();
        AssertLib.AssertHasNoDomainEvent(team);
        ResultMap<Void> rulesetSelection = team.selectCreationRuleset(ruleSet);
        Assertions.assertTrue(rulesetSelection.isSuccess());
        AssertLib.AssertHasDomainEventOfType(team, CreationRulesetSelectedEvent.class);
    }

    @Test
    @DisplayName("a Draft Team shall be given valid team creation ruleSet, shall fail if ruleset is not valid")
    void testChooseCreationRuleSetKO() {
        DraftTeam team = TeamCreator.createDraftTeam();
        Ruleset ruleSet = RulesetCreator.createBadTeamCreationRulset();
        AssertLib.AssertHasNoDomainEvent(team);
        ResultMap<Void> rulesetSelection = team.selectCreationRuleset(ruleSet);
        Assertions.assertTrue(rulesetSelection.isFailure());
        AssertLib.AssertHasNoDomainEvent(team);
    }

    @Test
    @DisplayName("A CreationRulesetChosenTeam shall be hydrated correctly")
    void testHydrateCreationRulesetChosenTeamOk() {
        DraftTeam baseTeam = TeamCreator.createDraftTeam();
        Ruleset ruleset = rulesetCreator.createBasicRuleset();

        CreationRulesetSelectedEvent tcEvt = new CreationRulesetSelectedEvent(baseTeam, ruleset);

        Result<AggregateRoot> hydratation = baseTeam.hydrate(List.of(tcEvt));
        Assertions.assertTrue(hydratation.isSuccess());

        CreationRulesetChosenTeam hydrated =(CreationRulesetChosenTeam) hydratation.getValue();
        Assertions.assertTrue(hydrated.isValid());
        assertEqualsResultset(hydrated);
    }
}
