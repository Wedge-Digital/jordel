package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.team_building.domain.Roster;
import com.bloodbowlclub.team_building.domain.RosterTier;
import com.bloodbowlclub.team_building.domain.TierID;
import com.bloodbowlclub.team_building.domain.TierName;

import java.util.List;

public class TierCreator {

    public static RosterTier createTier(List<Roster> rosterList) {
        return RosterTier.builder()
                .tierID(new TierID("01KCF2B2NQKT312ZQJC5ZVYTQ2"))
                .name(new TierName("Top Tier"))
                .rosterList(rosterList)
                .build();
    }
}
