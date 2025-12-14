package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.shared.roster.RosterID;
import com.bloodbowlclub.shared.roster.RosterName;
import com.bloodbowlclub.team_building.domain.Roster;

public class RosterCreator {

    public static Roster CreateRoster() {
        return Roster.builder()
                .rosterId(new RosterID("01KCCH67VJYCMKEN0R43F8KVWD"))
                .rosterName(new RosterName("Chaos Chosen"))
                .build();
    }
    public static Roster CreateRoster(String rosterName) {
        return Roster.builder()
                .rosterId(new RosterID("01KCE7R74NGMWMVYV76CFXZ70T"))
                .rosterName(new RosterName(rosterName))
                .build();
    }
}
