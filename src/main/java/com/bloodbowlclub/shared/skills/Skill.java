package com.bloodbowlclub.shared.skills;

public class Skill {

    private SkillID id;
    private SkillName name;
    private SkillDescription desc;
    private ESkillType type;

    private ESkillCategory category;

    public boolean isGeneral() {
        return this.category.equals(ESkillCategory.GENERAL);
    }

    public boolean isStandard() {
        return this.type.equals(ESkillType.STANDARD);
    }

    public boolean isElite() {
        return this.type.equals(ESkillType.ELITE);
    }
}
