package com.bloodbowlclub.team_building.domain.roster;

import com.bloodbowlclub.lib.domain.ValueObject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * Value Object représentant une limite croisée entre plusieurs positions de joueurs.
 * Par exemple : maximum 1 Big Guy parmi Troll, Ogre, Minotaur.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CrossLimit extends ValueObject<List<String>> {

    @Positive(message = "{crossLimit.limit.positive}")
    private int limit;

    @NotEmpty(message = "{crossLimit.limitedPlayerIds.notEmpty}")
    private List<String> limitedPlayerIds;

    /**
     * Vérifie si un joueur est concerné par cette limite croisée.
     *
     * @param playerDefinitionId L'ID du joueur à vérifier
     * @return true si le joueur est dans la liste des joueurs limités
     */
    public boolean includesPlayer(String playerDefinitionId) {
        if (limitedPlayerIds == null || limitedPlayerIds.isEmpty()) {
            return false;
        }
        return limitedPlayerIds.contains(playerDefinitionId);
    }

    /**
     * Vérifie si un joueur n'est PAS concerné par cette limite croisée.
     *
     * @param playerDefinitionId L'ID du joueur à vérifier
     * @return true si le joueur n'est pas dans la liste des joueurs limités
     */
    public boolean doesNotIncludePlayer(String playerDefinitionId) {
        return !includesPlayer(playerDefinitionId);
    }

    @Override
    public String toString() {
        return String.format("CrossLimit[limit=%d, players=%s]", limit, limitedPlayerIds);
    }

    @Override
    public boolean equalsString(String id) {
        // Un CrossLimit n'a pas d'ID unique, cette méthode n'est pas applicable
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrossLimit that = (CrossLimit) o;
        return limit == that.limit &&
               Objects.equals(limitedPlayerIds, that.limitedPlayerIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(limit, limitedPlayerIds);
    }
}
