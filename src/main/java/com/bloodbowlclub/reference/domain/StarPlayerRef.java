package com.bloodbowlclub.reference.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Représentation d'un Star Player dans les données de référence Blood Bowl.
 * Les Star Players sont des joueurs légendaires pouvant être recrutés par plusieurs équipes.
 * Immutable, chargée depuis JSON au démarrage.
 * ISOLÉE de team_building.domain.
 */
@Getter
@Builder
public class StarPlayerRef {

    @NotNull
    private final String uid;

    @NotNull
    private final String name;

    @Valid
    @NotNull
    private final Integer cost; // Prix en milliers de gold

    // Caractéristiques Blood Bowl
    @Valid
    @NotNull
    private final PlayerCharacteristic movement; // MA

    @Valid
    @NotNull
    private final PlayerCharacteristic strength; // ST

    @Valid
    @NotNull
    private final PlayerCharacteristic agility; // AG

    @Valid
    @NotNull
    private final PlayerCharacteristic passing; // PA (0 = pas de caractéristique)

    @Valid
    @NotNull
    private final PlayerCharacteristic armourValue; // AV

    // Type de joueur (ex: "Blitzer, Human", "Big Guy, Ogre", "Blocker, Human")
    private final String playerType;

    // Compétences
    @Valid
    private final List<SkillRefLight> skills;

    // Capacité spéciale unique
    @NotNull
    private final String specialAbilityName;

    @NotNull
    private final String specialAbilityDescription;

    // Ligues pour lesquelles ce star player peut jouer
    @Valid
    private final List<LeagueRef> playsFor;

    // Équipes pouvant recruter ce star player (UIDs des rosters)
    @Valid
    private final List<String> availableForRosters;

    // Méthodes métier
    public boolean hasSkill(String skillUid) {
        return skills != null && skills.stream().anyMatch(s -> s.getUid().equals(skillUid));
    }

    public boolean hasSkills() {
        return skills != null && !skills.isEmpty();
    }

    public boolean canPlayFor(String rosterUid) {
        return availableForRosters != null && availableForRosters.contains(rosterUid);
    }

    public boolean canPlayInLeague(String leagueUid) {
        return playsFor != null && playsFor.stream().anyMatch(l -> l.getUid().equals(leagueUid));
    }

    public int getCostInGold() {
        return cost * 1000;
    }
}