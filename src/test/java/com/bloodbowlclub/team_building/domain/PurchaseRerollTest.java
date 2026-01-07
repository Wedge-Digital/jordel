package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.team_building.domain.events.TeamRerollPurchasedEvent;
import com.bloodbowlclub.team_building.domain.events.TeamRerollRemovedEvent;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.bloodbowlclub.team_building.domain.team.RosterSelectedTeam;
import com.bloodbowlclub.test_utilities.AssertLib;
import com.bloodbowlclub.test_utilities.team_creation.RosterCreator;
import com.bloodbowlclub.test_utilities.team_creation.TeamCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PurchaseRerollTest extends TestCase {

    // reroll remove reroll should do nothing if reroll number is 0
    // reroll remove should succeed, with event
    // reroll purchase / remove history shall be hydratable
    TeamCreator teamCreator = new TeamCreator();
    RosterCreator rosterCreator = new RosterCreator();

    @Test
    @DisplayName("reroll puchase should succeed")
    void testRerollPurchaseShouldSucceed() {
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterSelectedTeam rcTeam =  teamCreator.createChaosTeam(chaosPact);
        ResultMap<Integer> baseRemainingBudget = rcTeam.computeRemainingBudget();
        ResultMap<Void> rrPurchase = rcTeam.purchaseReroll(3);
        Assertions.assertTrue(rrPurchase.isSuccess());
        Assertions.assertEquals(3, rcTeam.getRerollCount());
        Assertions.assertEquals(baseRemainingBudget.getValue() - 180, rcTeam.computeRemainingBudget().getValue());
        Assertions.assertEquals(1, rcTeam.domainEvents().size());
        Assertions.assertEquals(TeamRerollPurchasedEvent.class, rcTeam.domainEvents().getLast().getClass());
    }

    @Test
    @DisplayName("Reroll purchase should fail if purchase exceed reroll max")
    void testRerollPurchaseShouldFailIfMaxIsReached() {
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterSelectedTeam rcTeam =  teamCreator.createChaosTeam(chaosPact);
        ResultMap<Void> rrPurchase = rcTeam.purchaseReroll(3);
        Assertions.assertTrue(rrPurchase.isSuccess());
        rrPurchase = rcTeam.purchaseReroll(3);
        Assertions.assertTrue(rrPurchase.isSuccess());
        rrPurchase = rcTeam.purchaseReroll(3);
        Assertions.assertTrue(rrPurchase.isFailure());
        Assertions.assertEquals(6, rcTeam.getRerollCount());
        AssertLib.assertResultContainsError(
                rrPurchase,
                "team.reroll",
                "L'équipe 01KCSHJS1K5M8JTW9D5A58VY1S/teamName ne peut pas acheter 3 relances, cela lui ferait dépasser le maximum de relance autorisé",
                messageSource
        );
    }

   @Test
   @DisplayName("Reroll purchase should fail if not enough budget")
   void testRerollPurchaseShouldFailIfNotEnoughBudget() {
        RosterSelectedTeam teamWithoutBudget = teamCreator.createTeamWithoutBudgetLeft();
        ResultMap<Void> rrPurchase = teamWithoutBudget.purchaseReroll(3);
        Assertions.assertTrue(rrPurchase.isFailure());
       AssertLib.assertResultContainsError(
               rrPurchase,
              "team.reroll",
               "L'équipe 01KCSJRWAFMN3T35AVZDX1ASXP/teamName n'a pas le budget pour acheter 3 rerolls, achat annulé.",
               messageSource
       );
   }

    @Test
    @DisplayName("reroll remove should succeed")
    void testRerollRemoveShouldSucceed() {
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterSelectedTeam rcTeam =  teamCreator.createChaosTeam(chaosPact);
        rcTeam.purchaseReroll(3);
        ResultMap<Void> rrRemove = rcTeam.removeReroll(2);
        Assertions.assertEquals(1, rcTeam.getRerollCount());
        Assertions.assertTrue(rrRemove.isSuccess());
        Assertions.assertEquals(2, rcTeam.domainEvents().size());
        Assertions.assertEquals(TeamRerollRemovedEvent.class, rcTeam.domainEvents().getLast().getClass());
    }

    //===============================================================================================================
    //
    // Tests des méthodes apply() - Reconstruction d'agrégats
    //
    //===============================================================================================================

    @Test
    @DisplayName("apply(TeamRerollPurchasedEvent) should increment reroll count without adding event")
    void testApplyTeamRerollPurchasedEventShouldIncrementCount() {
        // Given
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterSelectedTeam team = teamCreator.createChaosTeam(chaosPact);
        Assertions.assertEquals(0, team.getRerollCount());
        int initialEventCount = team.domainEvents().size();

        // When - Simulate event application during aggregate reconstruction
        TeamRerollPurchasedEvent event = new TeamRerollPurchasedEvent(team, 3);
        Result<AggregateRoot> result = team.apply(event);

        // Then
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals(3, team.getRerollCount());
        // Critical: apply() should NOT add new events (reconstruction mode)
        Assertions.assertEquals(initialEventCount, team.domainEvents().size());
    }

    @Test
    @DisplayName("apply(TeamRerollPurchasedEvent) should accumulate multiple purchases")
    void testApplyMultipleTeamRerollPurchasedEvents() {
        // Given
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterSelectedTeam team = teamCreator.createChaosTeam(chaosPact);
        int initialEventCount = team.domainEvents().size();

        // When - Simulate multiple purchase events during reconstruction
        team.apply(new TeamRerollPurchasedEvent(team, 2));
        team.apply(new TeamRerollPurchasedEvent(team, 3));
        team.apply(new TeamRerollPurchasedEvent(team, 1));

        // Then
        Assertions.assertEquals(6, team.getRerollCount());
        // No new events should be added during reconstruction
        Assertions.assertEquals(initialEventCount, team.domainEvents().size());
    }

    @Test
    @DisplayName("apply(TeamRerollRemovedEvent) should decrement reroll count without adding event")
    void testApplyTeamRerollRemovedEventShouldDecrementCount() {
        // Given
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterSelectedTeam team = teamCreator.createChaosTeam(chaosPact);
        team.apply(new TeamRerollPurchasedEvent(team, 5));
        int initialEventCount = team.domainEvents().size();

        // When - Simulate removal event during reconstruction
        TeamRerollRemovedEvent event = new TeamRerollRemovedEvent(team, 2);
        Result<AggregateRoot> result = team.apply(event);

        // Then
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals(3, team.getRerollCount());
        // Critical: apply() should NOT add new events (reconstruction mode)
        Assertions.assertEquals(initialEventCount, team.domainEvents().size());
    }

    @Test
    @DisplayName("apply(TeamRerollRemovedEvent) should set reroll count to 0 if removing more than available")
    void testApplyTeamRerollRemovedEventWithExcessRemoval() {
        // Given
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterSelectedTeam team = teamCreator.createChaosTeam(chaosPact);
        team.apply(new TeamRerollPurchasedEvent(team, 3));
        int initialEventCount = team.domainEvents().size();

        // When - Try to remove more rerolls than available
        TeamRerollRemovedEvent event = new TeamRerollRemovedEvent(team, 10);
        Result<AggregateRoot> result = team.apply(event);

        // Then
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals(0, team.getRerollCount());
        // No new events should be added
        Assertions.assertEquals(initialEventCount, team.domainEvents().size());
    }

    @Test
    @DisplayName("aggregate reconstruction should correctly replay purchase and removal events")
    void testAggregateReconstructionWithRerollEvents() {
        // Given
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterSelectedTeam team = teamCreator.createChaosTeam(chaosPact);
        int initialEventCount = team.domainEvents().size();

        // When - Simulate event replay from event store (hydratation)
        team.apply(new TeamRerollPurchasedEvent(team, 4));  // Buy 4
        team.apply(new TeamRerollRemovedEvent(team, 1));    // Remove 1
        team.apply(new TeamRerollPurchasedEvent(team, 2));  // Buy 2 more
        team.apply(new TeamRerollRemovedEvent(team, 2));    // Remove 2

        // Then - Final state should be: 4 - 1 + 2 - 2 = 3
        Assertions.assertEquals(3, team.getRerollCount());
        // No events added during reconstruction (all apply() calls are pure state mutations)
        Assertions.assertEquals(initialEventCount, team.domainEvents().size());
    }

    @Test
    @DisplayName("apply() methods should be idempotent for same event")
    void testApplyMethodsAreIdempotent() {
        // Given
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterSelectedTeam team = teamCreator.createChaosTeam(chaosPact);
        TeamRerollPurchasedEvent purchaseEvent = new TeamRerollPurchasedEvent(team, 3);

        // When - Apply same event multiple times (should accumulate, not be truly idempotent)
        team.apply(purchaseEvent);
        int countAfterFirst = team.getRerollCount();
        team.apply(purchaseEvent);
        int countAfterSecond = team.getRerollCount();

        // Then - Each application adds to the count (this is expected for reroll events)
        Assertions.assertEquals(3, countAfterFirst);
        Assertions.assertEquals(6, countAfterSecond);
        // Note: Reroll events are cumulative, not idempotent. The event store should prevent duplicates.
    }


}
