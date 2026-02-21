package com.bloodbowlclub.ruleset_building.domain;

import com.bloodbowlclub.shared.inducement.InducementID;
import com.bloodbowlclub.shared.inducement.StarplayerID;
import com.bloodbowlclub.shared.roster.RosterID;
import com.bloodbowlclub.shared.ruleset.CreationBudget;
import lombok.Builder;

import java.util.List;

@Builder
public class RulesetTier {

    private CreationBudget budget;

    private CreationBudget skillBudget;

    private CreationBudget additionalBudget;

    private List<StarplayerID> allowedStarPlayers;

    private List<InducementID> allowedInducements;

    private List<RosterID> rosterList;

}
