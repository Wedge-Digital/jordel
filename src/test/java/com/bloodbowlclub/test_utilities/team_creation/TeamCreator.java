package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.shared.shared.cloudinary_url.CloudinaryUrl;
import com.bloodbowlclub.shared.team.TeamID;
import com.bloodbowlclub.shared.team.TeamName;
import com.bloodbowlclub.team_building.domain.commands.RegisterNewTeamCommand;
import com.bloodbowlclub.team_building.domain.events.CreationRulesetSelectedEvent;
import com.bloodbowlclub.team_building.domain.events.DraftTeamRegisteredEvent;
import com.bloodbowlclub.team_building.domain.roster.PlayerDefinition;
import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.bloodbowlclub.team_building.domain.ruleset.Ruleset;
import com.bloodbowlclub.team_building.domain.team.BaseTeam;
import com.bloodbowlclub.team_building.domain.team.CreationRulesetChosenTeam;
import com.bloodbowlclub.team_building.domain.team.DraftTeam;
import com.bloodbowlclub.team_building.domain.team.RosterChosenTeam;
import com.bloodbowlclub.test_utilities.cloudinary.CloudinaryUrlBuilder;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;

public class TeamCreator {
    RosterCreator rosterCreator = new RosterCreator();
    PlayerDefinitionCreator playerCreator = new PlayerDefinitionCreator();
    RulesetCreator rulesetCreator = new RulesetCreator();

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

    public static CreationRulesetChosenTeam createRulesetChosenTeam(Ruleset ruleset) {
        DraftTeam draftTeam = createDraftTeam();
        CreationRulesetSelectedEvent rcEvent = new CreationRulesetSelectedEvent(draftTeam, ruleset);
        return (CreationRulesetChosenTeam) draftTeam.apply(rcEvent).getValue();
    }

    public RosterChosenTeam createChaosTeam() {
        Roster chaos = rosterCreator.createChaosChosen();
        Ruleset ruleset = rulesetCreator.createBasicRuleset();
        return RosterChosenTeam.builder()
                .teamId(new TeamID("01KCYR7RR53308AA6TEMSVN15M"))
                .name(new TeamName("Chaos Team"))
                .logoUrl(new CloudinaryUrl("https://res.cloudinary.com/bloodbowlclub-com/image/upload/v1659445677/user_uploads/x44ke8sgvqrap91mry8i.jpg"))
                .roster(chaos)
                .ruleset(ruleset)
                .hiredPlayers(new ArrayList<>())
                .build();
    }

    public RosterChosenTeam createChaosTeam(Roster roster) {
        Ruleset ruleset = rulesetCreator.createBasicRuleset();
        RosterChosenTeam team = RosterChosenTeam.builder()
                .teamId(new TeamID("01KCSHJS1K5M8JTW9D5A58VY1S"))
                .name(new TeamName("teamName"))
                .logoUrl(new CloudinaryUrl("https://res.cloudinary.com/bloodbowlclub-com/image/upload/v1659445677/user_uploads/x44ke8sgvqrap91mry8i.jpg"))
                .roster(roster)
                .ruleset(ruleset)
                .hiredPlayers(new ArrayList<>())
                .build();
        return team;
    }

    public RosterChosenTeam createTeam(Roster roster, Ruleset ruleset) {
        RosterChosenTeam team = RosterChosenTeam.builder()
                .teamId(new TeamID("01KCSHJS1K5M8JTW9D5A58VY1S"))
                .name(new TeamName("teamName"))
                .logoUrl(new CloudinaryUrl("https://res.cloudinary.com/bloodbowlclub-com/image/upload/v1659445677/user_uploads/x44ke8sgvqrap91mry8i.jpg"))
                .hiredPlayers(new ArrayList<>())
                .ruleset(ruleset)
                .roster(roster)
                .build();
        return team;
    }

    public RosterChosenTeam createRosterTeamWith16Player() {
        ArrayList<PlayerDefinition> defs = new ArrayList<>();
        PlayerDefinition lineman = playerCreator.createWoodElfLineman();
        Ruleset ruleset = rulesetCreator.createBasicRuleset();
        for (int cpt=0; cpt<16; cpt++) {
            defs.add(lineman);
        }

        RosterChosenTeam team = RosterChosenTeam.builder()
                .teamId(new TeamID("01KCSJRWAFMN3T35AVZDX1ASXP"))
                .name(new TeamName("teamName"))
                .logoUrl(new CloudinaryUrl("https://res.cloudinary.com/bloodbowlclub-com/image/upload/v1659445677/user_uploads/x44ke8sgvqrap91mry8i.jpg"))
                .roster(rosterCreator.createWoodElves())
                .hiredPlayers(defs)
                .ruleset(ruleset)
                .build();
        return team;
    }
}
