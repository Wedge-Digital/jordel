package com.auth.services.refactored_errors;

import com.auth.services.MessageTranslator;

// Classe principale pour représenter une erreur
public class ApplicationError {
    private final Error type;
    private final String contextMessageKey;
    private final Object[] contextArgs;
    private final String rawContextMessage;
    private final Throwable cause;

    // Constructeur avec clé de traduction pour le contexte
    public ApplicationError(Error type, String contextMessageKey, Object... contextArgs) {
        this.type = type;
        this.contextMessageKey = contextMessageKey;
        this.contextArgs = contextArgs != null ? contextArgs.clone() : new Object[0];
        this.rawContextMessage = null;
        this.cause = null;
    }

    // Constructeur avec message brut pour le contexte
    public ApplicationError(Error type, String rawContextMessage) {
        this.type = type;
        this.contextMessageKey = null;
        this.contextArgs = new Object[0];
        this.rawContextMessage = rawContextMessage;
        this.cause = null;
    }

    // Constructeur avec cause
    public ApplicationError(Error type, String contextMessageKey, Throwable cause, Object... contextArgs) {
        this.type = type;
        this.contextMessageKey = contextMessageKey;
        this.contextArgs = contextArgs != null ? contextArgs.clone() : new Object[0];
        this.rawContextMessage = null;
        this.cause = cause;
    }

    // Getters
    public Error getType() {
        return type;
    }

    public String getContextMessageKey() {
        return contextMessageKey;
    }

    public Object[] getContextArgs() {
        return contextArgs.clone();
    }

    public String getRawContextMessage() {
        return rawContextMessage;
    }

    public Throwable getCause() {
        return cause;
    }

    // Méthode pour obtenir le message d'erreur complet traduit
    public String getTranslatedMessage(MessageTranslator translator) {
        String typeMessage = translator.translate(type.getMessageKey());
        String contextMessage = getTranslatedContextMessage(translator);

        if (contextMessage != null && !contextMessage.isEmpty()) {
            return typeMessage + " - " + contextMessage;
        }
        return typeMessage;
    }

    // Méthode pour obtenir seulement le message de contexte traduit
    public String getTranslatedContextMessage(MessageTranslator translator) {
        if (rawContextMessage != null) {
            return rawContextMessage;
        }
        if (contextMessageKey != null) {
            return translator.translate(contextMessageKey, contextArgs);
        }
        return null;
    }

    @Override
    public String toString() {
        return "ApplicationError{" +
                "type=" + type.getCode() +
                ", contextMessageKey='" + contextMessageKey + '\'' +
                ", rawContextMessage='" + rawContextMessage + '\'' +
                '}';
    }
}