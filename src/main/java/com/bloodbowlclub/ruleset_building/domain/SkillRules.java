package com.bloodbowlclub.ruleset_building.domain;

import com.bloodbowlclub.shared.skills.Skill;
import com.bloodbowlclub.shared.skills.SkillPrice;

import java.util.List;

public class SkillRules {

    private List<Skill> skillGroup;
    private SkillPrice price;

    private SkillLimit totalMaxSkill;

    private SkillLimit maxSameSkill;

    private SkillLimit maxPrimary;
    private SkillLimit maxSecondary;

    private SkillLimit maxStandard;
    private SkillLimit maxElite;
}
