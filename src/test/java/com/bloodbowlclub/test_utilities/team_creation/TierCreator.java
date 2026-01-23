package com.bloodbowlclub.test_utilities.team_creation;

import com.bloodbowlclub.team_building.domain.roster.Roster;
import com.bloodbowlclub.shared.ruleset.CreationBudget;
import com.bloodbowlclub.team_building.domain.ruleset.RosterTier;
import com.bloodbowlclub.shared.ruleset.TierID;
import com.bloodbowlclub.shared.ruleset.TierName;

import java.util.List;

public class TierCreator {

    public static RosterTier createTier(List<Roster> rosterList) {
        return RosterTier.builder()
                .tierID(new TierID("01KCF2B2NQKT312ZQJC5ZVYTQ2"))
                .name(new TierName("Top Tier"))
                .rosterList(rosterList)
                .build();
    }

    public RosterTier createInfiniteBudgetTier(List<Roster> rosterList) {
        return RosterTier.builder()
                .tierID(new TierID("01KCY8KRQMR7GMR8R2Y69X48JA"))
                .name(new TierName("Infinite Budget Tier"))
                .teamBudget(new CreationBudget(10050))
                .rosterList(rosterList)
                .build();
    }

    public RosterTier createTopTier(List<Roster> rosterList) {
        return RosterTier.builder()
                .tierID(new TierID("01KCY8KRQMR7GMR8R2Y69X48JA"))
                .name(new TierName("Top Tier"))
                .teamBudget(new CreationBudget(1050))
                .rosterList(rosterList)
                .build();
    }

    public RosterTier createMiddleTier(List<Roster> rosterList) {
        return RosterTier.builder()
                .tierID(new TierID("01KCY8M1561TDF2Q7RD4RWWTS7"))
                .name(new TierName("Middle Tier"))
                .teamBudget(new CreationBudget(1100))
                .rosterList(rosterList)
                .build();
    }
}
