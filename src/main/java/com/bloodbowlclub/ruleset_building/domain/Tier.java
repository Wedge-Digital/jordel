package com.bloodbowlclub.ruleset_building.domain;


import com.bloodbowlclub.shared.roster.RosterID;
import com.bloodbowlclub.shared.ruleset.CreationBudget;
import lombok.Builder;

import java.util.List;

@Builder
public class Tier {
    private List<RosterID> rosterList;

    private CreationBudget budget;

    private List<SkillRules> skillRules;
}
