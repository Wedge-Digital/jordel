package com.bloodbowlclub.reference.domain;

import com.bloodbowlclub.lib.domain.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public class SkillsList extends ValueObject {
    private final List<String> skillUids;

    public SkillsList(List<String> skillUids) {
        this.skillUids = skillUids != null ?
            Collections.unmodifiableList(new ArrayList<>(skillUids)) :
            Collections.emptyList();
    }

    public boolean isEmpty() {
        return skillUids.isEmpty();
    }

    public boolean contains(String skillUid) {
        return skillUids.contains(skillUid);
    }

    public int size() {
        return skillUids.size();
    }

    @Override
    public String toString() {
        return String.join(", ", skillUids);
    }

    @Override
    public boolean equalsString(String id) {
        return id.equals(value);
    }
}
