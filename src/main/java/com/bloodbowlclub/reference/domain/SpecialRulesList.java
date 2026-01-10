package com.bloodbowlclub.reference.domain;

import com.bloodbowlclub.lib.domain.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public class SpecialRulesList extends ValueObject {
    private final List<String> ruleUids;

    public SpecialRulesList(List<String> ruleUids) {
        this.ruleUids = ruleUids != null ?
            Collections.unmodifiableList(new ArrayList<>(ruleUids)) :
            Collections.emptyList();
    }

    public boolean isEmpty() {
        return ruleUids.isEmpty();
    }

    public int size() {
        return ruleUids.size();
    }

    public boolean contains(String ruleUid) {
        return ruleUids.contains(ruleUid);
    }

    @Override
    public String toString() {
        return String.join(", ", ruleUids);
    }

    @Override
    public boolean equalsString(String id) {
        return id.equals(value);
    }
}
