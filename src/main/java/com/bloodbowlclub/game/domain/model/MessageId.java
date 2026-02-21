package com.bloodbowlclub.game.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object - Message identifier
 */
public class MessageId {
    private final String value;

    private MessageId(String value) {
        this.value = Objects.requireNonNull(value, "MessageId cannot be null");
    }

    public static MessageId generate() {
        return new MessageId(UUID.randomUUID().toString());
    }

    public static MessageId of(String value) {
        return new MessageId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageId messageId = (MessageId) o;
        return value.equals(messageId.value);
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