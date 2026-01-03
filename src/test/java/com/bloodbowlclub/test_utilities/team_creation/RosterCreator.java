package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.shared.roster.RosterID;
import com.bloodbowlclub.shared.roster.RosterName;
import com.bloodbowlclub.team_building.domain.roster.*;
import com.bloodbowlclub.team_building.domain.team_staff.TeamStaff;

import java.util.List;

public class RosterCreator {

    private PlayerDefinitionCreator playerCreator = new PlayerDefinitionCreator();
    private PlayerDefinition human = playerCreator.createHuman();
    private PlayerDefinition warDancer = playerCreator.createWardancer();
    private PlayerDefinition witchElves = playerCreator.createWitchElf();
    private PlayerDefinition blitzer = playerCreator.createBlitzer();
    private PlayerDefinition assassin = playerCreator.createAssassin();
    private PlayerDefinition lineman = playerCreator.createLineman();

    private StaffCreator staffCreator = new StaffCreator();

    private List<TeamStaff> getFullTeamStaff() {
        TeamStaff cheers = staffCreator.createCheerleaders();
        TeamStaff apo = staffCreator.createApothecary();
        TeamStaff assistants = staffCreator.createCoachAssistant();
        return List.of(cheers, apo, assistants);
    }


    public Roster createChaosChosen() {
        PlayerDefinition minotaur = playerCreator.createMinotaur();
        return Roster.builder()
                .rosterId(new RosterID("CHAOS_CHOSEN"))
                .name(new RosterName("Chaos Chosen"))
                .playerDefinitions(List.of(minotaur))
                .allowedTeamStaff(getFullTeamStaff())
                .rerollPrice(new RerollBasePrice(70))
                .build();
    }

    public Roster createWoodElves() {
        return Roster.builder()
                .rosterId(new RosterID("WOOD_ELVES"))
                .name(new RosterName("Wood Elves"))
                .playerDefinitions(List.of(warDancer))
                .allowedTeamStaff(getFullTeamStaff())
                .rerollPrice(new RerollBasePrice(50))
                .build();
    }

    public Roster createDarkElves() {
        PlayerDefinition witches = playerCreator.createWitchElf();
        PlayerDefinition blitzer = playerCreator.createBlitzer();
        PlayerDefinition assassin = playerCreator.createAssassin();
        PlayerDefinition linemens = playerCreator.createLineman();
        return Roster.builder()
                .rosterId(new RosterID("DARK_ELVES"))
                .name(new RosterName("Dark Elves"))
                .playerDefinitions(List.of(witches, blitzer, assassin, linemens))
                .allowedTeamStaff(getFullTeamStaff())
                .rerollPrice(new RerollBasePrice(50))
                .build();
    }

    public Roster createProElves() {
        PlayerDefinition proElfBlitzer = playerCreator.createProElfBlitzer();
        return Roster.builder()
                .rosterId(new RosterID("PRO_ELVES"))
                .name(new RosterName("Pro Elves"))
                .playerDefinitions(List.of(proElfBlitzer))
                .allowedTeamStaff(getFullTeamStaff())
                .rerollPrice(new RerollBasePrice(50))
                .build();
    }

    public Roster createChaosPact() {
        PlayerDefinition human = playerCreator.createHuman();
        PlayerDefinition minotaur = playerCreator.createMinotaur();
        PlayerDefinition ogre = playerCreator.createOgre();
        PlayerDefinition troll = playerCreator.createTroll();
        PlayerDefinition ratOgre = playerCreator.createRatOgre();

        // Cross limit: max 3 Big Guys parmi Minotaur, Ogre, Troll, Rat Ogre
        CrossLimit bigGuysLimit = new CrossLimit(
                3,
                List.of(
                    minotaur.getId(),
                    ogre.getId(),
                    troll.getId(),
                    ratOgre.getId()
                )
        );

        return Roster.builder()
                .rosterId(new RosterID("CHAOS_PACT"))
                .name(new RosterName("Chaos Pact"))
                .playerDefinitions(List.of(human, minotaur, ogre, troll, ratOgre))
                .crossLimits(List.of(bigGuysLimit))
                .allowedTeamStaff(getFullTeamStaff())
                .rerollPrice(new RerollBasePrice(60))
                .build();
    }

    public Roster createUndead() {
        PlayerDefinition zombie = playerCreator.createZombie();
        PlayerDefinition ghoul = playerCreator.createGhoul();
        PlayerDefinition revenant = playerCreator.createRevenant();
        PlayerDefinition Mummy = playerCreator.createMummy();

        TeamStaff cheerleadr = staffCreator.createCheerleaders();
        TeamStaff coachAssistant = staffCreator.createCoachAssistant();
        return Roster.builder()
                .rosterId(new RosterID("UNDEAD"))
                .name(new RosterName("Undead"))
                .playerDefinitions(List.of(zombie, ghoul, revenant, Mummy))
                .crossLimits(List.of())
                .allowedTeamStaff(List.of(cheerleadr, coachAssistant))
                .rerollPrice(new RerollBasePrice(60))
                .build();

    }
}
