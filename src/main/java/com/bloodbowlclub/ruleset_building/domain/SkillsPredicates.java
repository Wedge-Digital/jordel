package com.bloodbowlclub.ruleset_building.domain;

import com.bloodbowlclub.shared.skills.Skill;

import java.util.function.Predicate;

public class SkillsPredicates {
    public static Predicate<Skill> standardSkills = Skill::isStandard;
    public static Predicate<Skill> eliteSkills = Skill::isElite;
}
