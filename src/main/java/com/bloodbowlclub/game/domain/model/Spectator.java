package com.bloodbowlclub.game.domain.model;

import java.util.Objects;

/**
 * Entity - Spectator in a game room
 */
public class Spectator {
    private final ParticipantId id;
    private final String displayName;

    public Spectator(ParticipantId id, String displayName) {
        this.id = Objects.requireNonNull(id, "Spectator id cannot be null");
        this.displayName = Objects.requireNonNull(displayName, "Display name cannot be null");
    }

    public ParticipantId getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spectator spectator = (Spectator) o;
        return id.equals(spectator.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}