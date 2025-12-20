package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.shared.roster.RosterID;
import com.bloodbowlclub.shared.roster.RosterName;
import com.bloodbowlclub.team_building.domain.roster.CrossLimit;
import com.bloodbowlclub.team_building.domain.roster.CrossLimitID;
import com.bloodbowlclub.team_building.domain.roster.PlayerDefinition;
import com.bloodbowlclub.team_building.domain.roster.Roster;

import java.util.List;

public class RosterCreator {

    private PlayerDefinitionCreator playerCreator = new PlayerDefinitionCreator();
    private PlayerDefinition human = playerCreator.createHuman();
    private PlayerDefinition warDancer = playerCreator.createWardancer();
    private PlayerDefinition witchElves = playerCreator.createWitchElf();
    private PlayerDefinition blitzer = playerCreator.createBlitzer();
    private PlayerDefinition assassin = playerCreator.createAssassin();
    private PlayerDefinition lineman = playerCreator.createLineman();


    public Roster createChaosChosen() {
        PlayerDefinition minotaur = playerCreator.createMinotaur();
        return Roster.builder()
                .rosterId(new RosterID("01KCCH67VJYCMKEN0R43F8KVWD"))
                .name(new RosterName("Chaos Chosen"))
                .playerDefinitions(List.of(minotaur))
                .build();
    }

    public Roster createWoodElves() {
        return Roster.builder()
                .rosterId(new RosterID("01KCEZ8SA4XBZF11V4QC0F8AJ3"))
                .name(new RosterName("Wood Elves"))
                .playerDefinitions(List.of(warDancer))
                .build();
    }

    public Roster createDarkElves() {
        PlayerDefinition witches = playerCreator.createWitchElf();
        PlayerDefinition blitzer = playerCreator.createBlitzer();
        PlayerDefinition assassin = playerCreator.createAssassin();
        PlayerDefinition linemens = playerCreator.createLineman();
        return Roster.builder()
                .rosterId(new RosterID("01KCF8ZPZMWRX0KDQ6AW3AMQVZ"))
                .name(new RosterName("Dark Elves"))
                .playerDefinitions(List.of(witches, blitzer, assassin, linemens))
                .build();
    }

    public Roster createProElves() {
        PlayerDefinition proElfBlitzer = playerCreator.createProElfBlitzer();
        return Roster.builder()
                .rosterId(new RosterID("01KCHTAHAJGV5DGV72XFPA79A0"))
                .name(new RosterName("Pro Elves"))
                .playerDefinitions(List.of(proElfBlitzer))
                .build();
    }

    public Roster createChaosPact() {
        PlayerDefinition human = playerCreator.createHuman();
        PlayerDefinition minotaur = playerCreator.createMinotaur();
        PlayerDefinition ogre = playerCreator.createOgre();
        PlayerDefinition troll = playerCreator.createTroll();
        PlayerDefinition ratOgre = playerCreator.createRatOgre();
        CrossLimit limit = CrossLimit.builder()
                .crossLimitID(new CrossLimitID("01KCZ08V7R2GRCKSYBCCRKDGDT"))
                .limitedPlayers(List.of(minotaur,ogre,troll,ratOgre))
                .limit(3)
                .build();
        return Roster.builder()
                .rosterId(new RosterID("01KCSGB4E6XWNQNVAQN3PSXKGB"))
                .name(new RosterName("Chaos Pact"))
                .playerDefinitions(List.of(human, minotaur, ogre, troll, ratOgre))
                .crossLimit(limit)
                .build();
    }
}
