package com.auth.services.refactored_errors;

// Interface pour les types d'erreurs
public class Error {
    private final String code;
    private final String messageKey;

    public String getCode() {
     return code;
    };

    public String getMessageKey() {
        return messageKey;
    }

    public Error(String code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }
}