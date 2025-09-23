package com.auth.services;

// Interface pour la traduction des messages
public interface MessageTranslator {
    String translate(String key, Object... args);
    String translate(String key, String defaultMessage, Object... args);
}
