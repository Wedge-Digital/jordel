package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.team_building.domain.BaseTeam;
import com.bloodbowlclub.team_building.domain.DraftTeam;
import com.bloodbowlclub.team_building.domain.Roster;
import com.bloodbowlclub.team_building.domain.RosterChosenTeam;
import com.bloodbowlclub.team_building.domain.commands.RegisterNewTeamCommand;
import com.bloodbowlclub.team_building.domain.events.DraftTeamRegisteredEvent;
import com.bloodbowlclub.team_building.domain.events.RosterChosenEvent;
import com.bloodbowlclub.test_utilities.cloudinary.CloudinaryUrlBuilder;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class TeamCreator {
    public static BaseTeam createBaseTeam() {
        BaseTeam baseTeam = new BaseTeam();
        Assertions.assertEquals(0, baseTeam.domainEvents().size());
        RegisterNewTeamCommand newTeamCommand = new RegisterNewTeamCommand("01KCAA6DBY2B3M8TKEV7GH5JNN", "Team Name", CloudinaryUrlBuilder.validUrl);
        ResultMap<Void> teamRegistration = baseTeam.registerNewTeam(newTeamCommand);
        Assertions.assertTrue(teamRegistration.isSuccess());
        Assertions.assertEquals(1, baseTeam.domainEvents().size());
        return baseTeam;
    }

    public static DraftTeam hydrateDraftTeam() {
        DraftTeamRegisteredEvent regEvent = new DraftTeamRegisteredEvent(createBaseTeam());
        BaseTeam baseTeam = new BaseTeam();
        return (DraftTeam) baseTeam.hydrate(List.of(regEvent)).getValue();
    }

    public static RosterChosenTeam hydrateChosenRosterTeam() {
        Roster chaos = RosterCreator.CreateRoster();
        BaseTeam baseTeam = createBaseTeam();
        DraftTeamRegisteredEvent regEvent = new DraftTeamRegisteredEvent(baseTeam);
        RosterChosenEvent rcEvent = new RosterChosenEvent(regEvent.getTeam(), chaos);
        return (RosterChosenTeam) baseTeam.hydrate(List.of(regEvent, rcEvent)).getValue();
    }
}
