package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.team_building.domain.events.TeamStaffPurchasedEvent;
import com.bloodbowlclub.team_building.domain.events.TeamStaffRemovedEvent;
import com.bloodbowlclub.team_building.domain.team.RosterSelectedTeam;
import com.bloodbowlclub.team_building.domain.team_staff.TeamStaff;
import com.bloodbowlclub.test_utilities.AssertLib;
import com.bloodbowlclub.test_utilities.team_creation.StaffCreator;
import com.bloodbowlclub.test_utilities.team_creation.TeamCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PurchaseTeamStaffTests extends TestCase {

    TeamCreator teamCreator = new TeamCreator();
    StaffCreator staffCreator = new StaffCreator();
    TeamStaff cheerleader = staffCreator.createCheerleaders();
    TeamStaff apothecary = staffCreator.createApothecary();

    @Test
    @DisplayName("Buy staff should succed")
    void testBuystaffShouldSucceed() {
        RosterSelectedTeam team = teamCreator.createChaosTeam();
        ResultMap<Void> staffBuying = team.buyStaff(cheerleader);
        Assertions.assertTrue(staffBuying.isSuccess());
        Assertions.assertEquals(10, team.getStaffBudget());
        Assertions.assertEquals(1, team.getCheerleaders());
        Assertions.assertEquals(1, team.domainEvents().size());
        Assertions.assertEquals(TeamStaffPurchasedEvent.class, team.domainEvents().getLast().getClass());
    }

    @Test
    @DisplayName("remove staff should succeed if staff is bought")
    void testRemovestaffSucceed() {
        RosterSelectedTeam team = teamCreator.createChaosTeam();
        ResultMap<Void> staffBuying = team.buyStaff(cheerleader);
        Assertions.assertTrue(staffBuying.isSuccess());

        ResultMap<Void> staffRemoving = team.removeStaff(cheerleader);
        Assertions.assertTrue(staffRemoving.isSuccess());
        Assertions.assertEquals(0, team.getCheerleaders());
        Assertions.assertEquals(2, team.domainEvents().size());
        Assertions.assertEquals(TeamStaffRemovedEvent.class, team.domainEvents().getLast().getClass());

    }

    @Test
    @DisplayName("buy staff should fail id budget is not sufficient")
    void testBuyShouldFailIfNotEnoughBudgetRemaining() {
        RosterSelectedTeam team = teamCreator.createTeamWithoutBudgetLeft();
        ResultMap<Void> staffBuying = team.buyStaff(cheerleader);
        Assertions.assertTrue(staffBuying.isFailure());
        AssertLib.assertResultContainsError(
                staffBuying,
                "team.staff",
                "la team 01KCSJRWAFMN3T35AVZDX1ASXP/teamName ne dispose pas d'un budget suffisant pour recruter le staff CHEERLEADERS/Cheerleaders",
                messageSource
        );
    }

    @Test
    @DisplayName("buy staff should fail if max staff is already reached")
    void testBuyShouldFailIfMaxIsReached() {
        RosterSelectedTeam team = teamCreator.createTeamWithMaxCheerleaders();
        ResultMap<Void> staffBuying = team.buyStaff(cheerleader);
        Assertions.assertTrue(staffBuying.isFailure());
        AssertLib.assertResultContainsError(
                staffBuying,
                "team.staff",
                "Le max de staff d'equipe CHEERLEADERS/Cheerleaders déjà atteint (6) pour l'équipe 01KCSJRWAFMN3T35AVZDX1ASXP/teamName. Achat impossible.",
                messageSource
        );
    }

    @Test
    @DisplayName("buy apo should fails if roster doesn't allow apo")
    void ApoRecruitShouldFailIfApoIsNotAllowed() {
        RosterSelectedTeam team = teamCreator.createUndeadCandidateTeam();
        ResultMap<Void> staffBuying = team.buyStaff(apothecary);
        Assertions.assertTrue(staffBuying.isFailure());
        AssertLib.assertResultContainsError(
                staffBuying,
                "team.staff",
                "Le staff APOTHECARY/Apothecary n'est pas autorisé pour l'équipe 01KD3NKX4S6FYBYZDN59YQDZ8J/undead team, achat impossible.",
                messageSource
        );
    }
}
