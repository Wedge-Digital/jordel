package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.shared.roster.RosterID;
import com.bloodbowlclub.shared.roster.RosterName;
import com.bloodbowlclub.team_building.domain.Roster;

public class RosterCreator {

    public static Roster createRoster() {
        return Roster.builder()
                .rosterId(new RosterID("01KCCH67VJYCMKEN0R43F8KVWD"))
                .rosterName(new RosterName("Chaos Chosen"))
                .build();
    }
    public static Roster createRoster(String rosterName) {
        return Roster.builder()
                .rosterId(new RosterID("01KCE7R74NGMWMVYV76CFXZ70T"))
                .rosterName(new RosterName(rosterName))
                .build();
    }

    public static Roster createWoodElves() {
        return Roster.builder()
                .rosterId(new RosterID("01KCEZ8SA4XBZF11V4QC0F8AJ3"))
                .rosterName(new RosterName("Wood Elves"))
                .build();
    }

    public static Roster createDarkElves() {
        return Roster.builder()
                .rosterId(new RosterID("01KCF8ZPZMWRX0KDQ6AW3AMQVZ"))
                .rosterName(new RosterName("Dark Elves"))
                .build();
    }
}
