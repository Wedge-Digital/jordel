package com.bloodbowlclub.reference.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Représentation d'une league dans les données de référence Blood Bowl.
 * Immutable, chargée depuis JSON au démarrage.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeagueRef {
    @NotNull
    private String uid;

    @NotNull
    private String label;
}
