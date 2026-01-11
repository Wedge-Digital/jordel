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
    private final List<SpecialRuleRef> ruleList;

    public SpecialRulesList(List<SpecialRuleRef> ruleList) {
        this.ruleList = ruleList != null ?
            Collections.unmodifiableList(new ArrayList<>(ruleList)) :
            Collections.emptyList();
    }

    public boolean isEmpty() {
        return ruleList.isEmpty();
    }

    public int size() {
        return ruleList.size();
    }

    public boolean contains(String ruleUid) {
        return ruleList.contains(ruleUid);
    }

    @Override
    public String toString() {
        return ruleList.stream().map(item -> String.join(" ,", item.getLabel() )).toString();
    }

    @Override
    public boolean equalsString(String id) {
        return id.equals(value);
    }
}
