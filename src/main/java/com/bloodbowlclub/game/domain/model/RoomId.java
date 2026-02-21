package com.bloodbowlclub.game.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object - Room identifier
 */
public class RoomId {
    private final String value;

    private RoomId(String value) {
        this.value = Objects.requireNonNull(value, "RoomId cannot be null");
    }

    public static RoomId generate() {
        return new RoomId(UUID.randomUUID().toString());
    }

    public static RoomId of(String value) {
        return new RoomId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomId roomId = (RoomId) o;
        return value.equals(roomId.value);
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
