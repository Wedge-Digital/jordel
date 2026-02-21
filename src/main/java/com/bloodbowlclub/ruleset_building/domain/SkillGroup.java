package com.bloodbowlclub.ruleset_building.domain;

import com.bloodbowlclub.shared.skills.Skill;
import lombok.Builder;

import java.util.function.Predicate;

@Builder
public class SkillGroup {
    private SkillCost primaryCost;

    private SkillCost secondaryCost;

    private Predicate<Skill> skillSelector;

}
