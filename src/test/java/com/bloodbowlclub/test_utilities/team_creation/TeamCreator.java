package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.team_building.domain.*;
import com.bloodbowlclub.team_building.domain.commands.RegisterNewTeamCommand;
import com.bloodbowlclub.team_building.domain.events.CreationRulesetSelectedEvent;
import com.bloodbowlclub.team_building.domain.events.DraftTeamRegisteredEvent;
import com.bloodbowlclub.team_building.domain.events.RosterChosenEvent;
import com.bloodbowlclub.test_utilities.cloudinary.CloudinaryUrlBuilder;
import org.junit.jupiter.api.Assertions;

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

    public static DraftTeam createDraftTeam() {
        BaseTeam baseTeam = createBaseTeam();
        DraftTeamRegisteredEvent regEvent = new DraftTeamRegisteredEvent(baseTeam);
        return (DraftTeam) baseTeam.apply(regEvent).getValue();
    }

    public static CreationRulesetChosenTeam createRulesetChosenTeam() {
        TeamCreationRuleset ruleset = TeamCreationRulesetCreator.createTeamCreationRulset();

        DraftTeam draftTeam = createDraftTeam();
        CreationRulesetSelectedEvent rcEvent = new CreationRulesetSelectedEvent(draftTeam, ruleset);

        return (CreationRulesetChosenTeam) draftTeam.apply(rcEvent).getValue();
    }
    public static CreationRulesetChosenTeam createRulesetChosenTeam(TeamCreationRuleset ruleset) {
        DraftTeam draftTeam = createDraftTeam();
        CreationRulesetSelectedEvent rcEvent = new CreationRulesetSelectedEvent(draftTeam, ruleset);
        return (CreationRulesetChosenTeam) draftTeam.apply(rcEvent).getValue();
    }

    public static RosterChosenTeam createRosterChosenTeam() {
        Roster chaos = RosterCreator.createRoster();
        CreationRulesetChosenTeam team = createRulesetChosenTeam();
        RosterChosenEvent rcEvent = new RosterChosenEvent(team, chaos);
        return (RosterChosenTeam) team.apply(rcEvent).getValue();
    }

}
