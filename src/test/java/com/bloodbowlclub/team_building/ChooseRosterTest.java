package com.bloodbowlclub.team_building;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.team_building.domain.BaseTeam;
import com.bloodbowlclub.team_building.domain.DraftTeam;
import com.bloodbowlclub.team_building.domain.Roster;
import com.bloodbowlclub.team_building.domain.RosterChosenTeam;
import com.bloodbowlclub.team_building.domain.events.DraftTeamRegisteredEvent;
import com.bloodbowlclub.team_building.domain.events.RosterChosenEvent;
import com.bloodbowlclub.test_utilities.AssertLib;
import com.bloodbowlclub.test_utilities.team_creation.RosterCreator;
import com.bloodbowlclub.test_utilities.team_creation.TeamCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ChooseRosterTest extends TestCase {

    @Test
    @DisplayName("")
    void testChooseRosterSucceed() {
        Roster chaos = RosterCreator.CreateRoster();
        DraftTeam team = TeamCreator.hydrateDraftTeam();
        AssertLib.AssertHasNoDomainEvent(team);
        ResultMap<Void> rosterChoice = team.chooseRoster(chaos);
        Assertions.assertTrue(rosterChoice.isSuccess());
        AssertLib.AssertHasDomainEventOfType(team, RosterChosenEvent.class);
    }

    @Test
    @DisplayName("Hydrate a ChosenRoster Team shall be Ok")
    void testChooseUnknonwnRosterShallFail() {
        RosterChosenTeam rcTeam = TeamCreator.hydrateChosenRosterTeam();
        Assertions.assertTrue(rcTeam.isValid());
        Assertions.assertTrue(rcTeam.isDraftTeam());
        Assertions.assertTrue(rcTeam.isRosterChosen());
        AssertLib.AssertHasNoDomainEvent(rcTeam);
        assertEqualsResultset(rcTeam);
    }

    @Test
    @DisplayName("change roster shall be possible")
    void testChangeRosterSucceed() {
        RosterChosenTeam rcTeam = TeamCreator.hydrateChosenRosterTeam();
        AssertLib.AssertHasNoDomainEvent(rcTeam);
        Roster anotherRoster = RosterCreator.CreateRoster("AnotherRoster");
        ResultMap<Void> rosterChoice = rcTeam.chooseRoster(anotherRoster);
        Assertions.assertTrue(rosterChoice.isSuccess());
        AssertLib.AssertHasDomainEventOfType(rcTeam, RosterChosenEvent.class);
    }

    @Test
    @DisplayName("Hydratation of a team with changed roster shall be ok")
    void testChangeRosterHydratationShallBeOk() {
        Roster chaos = RosterCreator.CreateRoster();
        Roster anotherRoster = RosterCreator.CreateRoster("anotherRoster");
        BaseTeam baseTeam = TeamCreator.createBaseTeam();
        DraftTeamRegisteredEvent regEvent = new DraftTeamRegisteredEvent(baseTeam);
        RosterChosenEvent rcEvent = new RosterChosenEvent(regEvent.getTeam(), chaos);
        RosterChosenEvent rcEvent2 = new RosterChosenEvent(regEvent.getTeam(), anotherRoster);
        Result<AggregateRoot> hydratation = baseTeam.hydrate(List.of(regEvent, rcEvent, rcEvent2));
        Assertions.assertTrue(hydratation.isSuccess());
        RosterChosenTeam hydrated = (RosterChosenTeam) hydratation.getValue();
        Assertions.assertTrue(hydrated.getRoster().getRosterName().equalsString("anotherRoster"));
        assertEqualsResultset(hydrated);
    }

}
