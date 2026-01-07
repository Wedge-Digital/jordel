package com.bloodbowlclub.lib.services;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a translatable message that can be resolved to different locales.
 * This class stores a message key and optional parameters, allowing delayed
 * message resolution until the appropriate locale is known.
 */
public class TranslatableMessage {
    private final String messageKey;
    private final Object[] params;

    /**
     * Creates a translatable message with a message key and parameters.
     *
     * @param messageKey The message key to look up in resource bundles
     * @param params Optional parameters to inject into the message
     */
    public TranslatableMessage(String messageKey, Object... params) {
        Objects.requireNonNull(messageKey, "Message key cannot be null");
        this.messageKey = messageKey;
        this.params = params != null ? params : new Object[0];
    }

    /**
     * @return The message key used to look up the message in resource bundles
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * @return The parameters to be injected into the resolved message
     */
    public Object[] getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslatableMessage that = (TranslatableMessage) o;
        return Objects.equals(messageKey, that.messageKey) &&
               Arrays.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(messageKey);
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }

    @Override
    public String toString() {
        return "TranslatableMessage{" +
                "messageKey='" + messageKey + '\'' +
                ", params=" + Arrays.toString(params) +
                '}';
    }
}
