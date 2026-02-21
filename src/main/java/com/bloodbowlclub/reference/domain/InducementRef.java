package com.bloodbowlclub.reference.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Representation d'un Inducement dans les donnees de reference Blood Bowl.
 * Les Inducements sont des bonus temporaires achetables avant un match.
 * Immutable, chargee depuis JSON au demarrage.
 */
@Getter
@Builder
public class InducementRef {

    @NotNull
    private final String uid;

    @NotNull
    private final String name;

    @NotNull
    private final Integer cost; // Prix en milliers de gold

    private final Integer reducedCost; // Prix reduit en milliers de gold (si applicable)

    @NotNull
    private final Integer maxQuantity;

    @NotNull
    private final String category; // COMMON, SPECIALIZED, INFAMOUS_STAFF, WIZARD, BIASED_REFEREE

    private final List<String> restrictedTo; // Liste des special rules/conditions requises (vide = toutes equipes)

    private final List<String> reducedCostFor; // Liste des special rules donnant le cout reduit

    private final String description;

    // Methodes metier
    public boolean hasReducedCost() {
        return reducedCost != null && reducedCost > 0;
    }

    public boolean isRestricted() {
        return restrictedTo != null && !restrictedTo.isEmpty();
    }

    public boolean isAvailableFor(String specialRule) {
        if (!isRestricted()) {
            return true;
        }
        return restrictedTo.contains(specialRule);
    }

    public boolean hasReducedCostFor(String specialRule) {
        if (reducedCostFor == null || reducedCostFor.isEmpty()) {
            return false;
        }
        return reducedCostFor.contains(specialRule);
    }

    public int getCostInGold() {
        return cost * 1000;
    }

    public int getReducedCostInGold() {
        return reducedCost != null ? reducedCost * 1000 : 0;
    }

    public int getCostForTeam(List<String> teamSpecialRules) {
        if (teamSpecialRules == null || reducedCostFor == null) {
            return cost;
        }
        for (String rule : teamSpecialRules) {
            if (reducedCostFor.contains(rule)) {
                return reducedCost != null ? reducedCost : cost;
            }
        }
        return cost;
    }

    public boolean isCommon() {
        return "COMMON".equals(category);
    }

    public boolean isSpecialized() {
        return "SPECIALIZED".equals(category);
    }

    public boolean isInfamousStaff() {
        return "INFAMOUS_STAFF".equals(category);
    }

    public boolean isWizard() {
        return "WIZARD".equals(category);
    }

    public boolean isBiasedReferee() {
        return "BIASED_REFEREE".equals(category);
    }
}
