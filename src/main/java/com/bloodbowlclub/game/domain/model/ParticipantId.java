package com.bloodbowlclub.game.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object - Participant identifier (can be Player or Spectator)
 */
public class ParticipantId {
    private final String value;

    private ParticipantId(String value) {
        this.value = Objects.requireNonNull(value, "ParticipantId cannot be null");
    }

    public static ParticipantId generate() {
        return new ParticipantId(UUID.randomUUID().toString());
    }

    public static ParticipantId of(String value) {
        return new ParticipantId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantId that = (ParticipantId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
