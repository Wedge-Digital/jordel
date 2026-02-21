package com.bloodbowlclub.ruleset_building.domain;

import com.bloodbowlclub.lib.domain.ValueObject;

public class SkillCost extends ValueObject<Integer> {
    private int value;

    public SkillCost(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public boolean equalsString(String id) {
        return false;
    }
}
