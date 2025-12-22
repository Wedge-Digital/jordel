package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.team_building.domain.events.TeamStaffPurchasedEvent;
import com.bloodbowlclub.team_building.domain.events.TeamStaffRemovedEvent;
import com.bloodbowlclub.team_building.domain.team.RosterSelectedTeam;
import com.bloodbowlclub.team_building.domain.team_stuff.TeamStaff;
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
    @DisplayName("Buy Stuff should succed")
    void testBuyStuffShouldSucceed() {
        RosterSelectedTeam team = teamCreator.createChaosTeam();
        ResultMap<Void> stuffBuying = team.buyStaff(cheerleader, messageSource);
        Assertions.assertTrue(stuffBuying.isSuccess());
        Assertions.assertEquals(10, team.getStaffBudget());
        Assertions.assertEquals(1, team.getCheerleaders());
        Assertions.assertEquals(1, team.domainEvents().size());
        Assertions.assertEquals(TeamStaffPurchasedEvent.class, team.domainEvents().getLast().getClass());
    }

    @Test
    @DisplayName("remove stuff should succeed if stuff is bought")
    void testRemoveStuffSucceed() {
        RosterSelectedTeam team = teamCreator.createChaosTeam();
        ResultMap<Void> stuffBuying = team.buyStaff(cheerleader, messageSource);
        Assertions.assertTrue(stuffBuying.isSuccess());

        ResultMap<Void> stuffRemoving = team.removeStaff(cheerleader, messageSource);
        Assertions.assertTrue(stuffRemoving.isSuccess());
        Assertions.assertEquals(0, team.getCheerleaders());
        Assertions.assertEquals(2, team.domainEvents().size());
        Assertions.assertEquals(TeamStaffRemovedEvent.class, team.domainEvents().getLast().getClass());

    }

    @Test
    @DisplayName("buy stuff should fail id budget is not sufficient")
    void testBuyShouldFailIfNotEnoughBudgetRemaining() {
        RosterSelectedTeam team = teamCreator.createTeamWithoutBudgetLeft();
        ResultMap<Void> stuffBuying = team.buyStaff(cheerleader, messageSource);
        Assertions.assertTrue(stuffBuying.isFailure());
        Assertions.assertEquals("team.stuff:la team 01KCSJRWAFMN3T35AVZDX1ASXP/teamName ne dispose pas d'un budget suffisant pour recruter le staff 01KD333HSX1F82N7XPJ3S14YKH/Cheerleaders", stuffBuying.getError());
    }

    @Test
    @DisplayName("buy stuff should fail if max stuff is already reached")
    void testBuyShouldFailIfMaxIsReached() {
        RosterSelectedTeam team = teamCreator.createTeamWithMaxCheerleaders();
        ResultMap<Void> stuffBuying = team.buyStaff(cheerleader, messageSource);
        Assertions.assertTrue(stuffBuying.isFailure());
        Assertions.assertEquals("team.stuff:Le max de staff d'equipe 01KD333HSX1F82N7XPJ3S14YKH/Cheerleaders déjà atteint (6) pour l'équipe 01KCSJRWAFMN3T35AVZDX1ASXP/teamName. Achat impossible.", stuffBuying.getError());
    }

    @Test
    @DisplayName("buy apo should fails if roster doesn't allow apo")
    void ApoRecruitShouldFailIfApoIsNotAllowed() {
        RosterSelectedTeam team = teamCreator.createUndeadCandidateTeam();
        ResultMap<Void> stuffBuying = team.buyStaff(apothecary, messageSource);
        Assertions.assertTrue(stuffBuying.isFailure());
        Assertions.assertEquals("team.stuff:Le staff 01KD3PDE0W0P8EXM72KKN20RF6/Apothecary n'est pas autorisé pour l'équipe 01KD3NKX4S6FYBYZDN59YQDZ8J/undead team, achat impossible.", stuffBuying.getError());
    }
}
