package com.bloodbowlclub.reference.domain;

import com.bloodbowlclub.lib.domain.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente les catégories d'accès aux compétences (Primary ou Secondary).
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class SkillAccessCategories extends ValueObject {
    private final List<String> categories;

    public SkillAccessCategories(List<String> categories) {
        this.categories = categories != null ?
            Collections.unmodifiableList(new ArrayList<>(categories)) :
            Collections.emptyList();
    }

    public boolean isEmpty() {
        return categories.isEmpty();
    }

    public boolean contains(String category) {
        return categories.contains(category);
    }

    public int size() {
        return categories.size();
    }

    @Override
    public String toString() {
        return String.join(", ", categories);
    }

    @Override
    public boolean equalsString(String id) {
        return id.equals(value);
    }
}
