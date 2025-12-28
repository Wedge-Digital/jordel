package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.team_building.domain.events.TeamRerollPurchasedEvent;
import com.bloodbowlclub.team_building.domain.events.TeamRerollRemovedEvent;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.bloodbowlclub.team_building.domain.team.RosterSelectedTeam;
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
        ResultMap<Integer> baseRemainingBudget = rcTeam.computeRemainingBudget(messageSource);
        ResultMap<Void> rrPurchase = rcTeam.purchaseReroll(3 ,messageSource);
        Assertions.assertTrue(rrPurchase.isSuccess());
        Assertions.assertEquals(3, rcTeam.getRerollCount());
        Assertions.assertEquals(baseRemainingBudget.getValue() - 180, rcTeam.computeRemainingBudget(messageSource).getValue());
        Assertions.assertEquals(1, rcTeam.domainEvents().size());
        Assertions.assertEquals(TeamRerollPurchasedEvent.class, rcTeam.domainEvents().getLast().getClass());
    }

    @Test
    @DisplayName("Reroll purchase should fail if purchase exceed reroll max")
    void testRerollPurchaseShouldFailIfMaxIsReached() {
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterSelectedTeam rcTeam =  teamCreator.createChaosTeam(chaosPact);
        ResultMap<Void> rrPurchase = rcTeam.purchaseReroll(3 ,messageSource);
        Assertions.assertTrue(rrPurchase.isSuccess());
        rrPurchase = rcTeam.purchaseReroll(3 ,messageSource);
        Assertions.assertTrue(rrPurchase.isSuccess());
        rrPurchase = rcTeam.purchaseReroll(3 ,messageSource);
        Assertions.assertTrue(rrPurchase.isFailure());
        Assertions.assertEquals(6, rcTeam.getRerollCount());
        Assertions.assertEquals("team.reroll:L'équipe 01KCSHJS1K5M8JTW9D5A58VY1S/teamName ne peut pas acheter 3 relances, cela lui ferait dépasser le maximum de relance autorisé", rrPurchase.getError());
    }

   @Test
   @DisplayName("Reroll purchase should fail if not enough budget")
   void testRerollPurchaseShouldFailIfNotEnoughBudget() {
        RosterSelectedTeam teamWithoutBudget = teamCreator.createTeamWithoutBudgetLeft();
        ResultMap<Void> rrPurchase = teamWithoutBudget.purchaseReroll(3 ,messageSource);
        Assertions.assertTrue(rrPurchase.isFailure());
       Assertions.assertEquals("team.reroll:L'équipe 01KCSJRWAFMN3T35AVZDX1ASXP/teamName n'a pas le budget pour acheter 3 rerolls, achat annulé.", rrPurchase.getError());
   }

    @Test
    @DisplayName("reroll remove should succeed")
    void testRerollRemoveShouldSucceed() {
        Roster chaosPact = rosterCreator.createChaosPact();
        RosterSelectedTeam rcTeam =  teamCreator.createChaosTeam(chaosPact);
        rcTeam.purchaseReroll(3 ,messageSource);
        ResultMap<Void> rrRemove = rcTeam.removeReroll(2, messageSource);
        Assertions.assertEquals(1, rcTeam.getRerollCount());
        Assertions.assertTrue(rrRemove.isSuccess());
        Assertions.assertEquals(2, rcTeam.domainEvents().size());
        Assertions.assertEquals(TeamRerollRemovedEvent.class, rcTeam.domainEvents().getLast().getClass());
    }


}
