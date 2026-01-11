package com.bloodbowlclub.reference.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Version allégée d'une skill pour les rosters.
 * Contient uniquement les informations essentielles pour l'affichage dans un roster.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SkillRefLight {
    @NotNull
    private String uid;

    @NotNull
    private String name;
}
