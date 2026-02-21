package com.bloodbowlclub.game.domain.model;

import java.util.Objects;

/**
 * Entity - Player in a game room
 */
public class Player {
    private final ParticipantId id;
    private final String displayName;
    private final int playerNumber; // 1 or 2

    public Player(ParticipantId id, String displayName, int playerNumber) {
        this.id = Objects.requireNonNull(id, "Player id cannot be null");
        this.displayName = Objects.requireNonNull(displayName, "Display name cannot be null");

        if (playerNumber < 1 || playerNumber > 2) {
            throw new IllegalArgumentException("Player number must be 1 or 2");
        }
        this.playerNumber = playerNumber;
    }

    public ParticipantId getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id.equals(player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}