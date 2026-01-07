package com.bloodbowlclub.lib.services.result;

import com.bloodbowlclub.lib.services.TranslatableMessage;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class Result<T> {
    private final T value;
    private final TranslatableMessage errors;
    private final ErrorCode errorCode;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Result.class);

    // Constructeur privé pour forcer l'utilisation des méthodes statiques
    private Result(T value, TranslatableMessage errors, ErrorCode errorCode) {
        this.value = value;
        this.errors = errors;
        this.errorCode = errorCode;
    }

    // Méthode statique pour créer un résultat réussi
    public static <T> Result<T> success(T value) {
        return new Result<>(value, null, null);
    }

    // Méthode statique pour créer un résultat échoué
    public static <T> Result<T> failure(TranslatableMessage error, ErrorCode errorCode) {
        logger.error("##### RESULT ERROR ######### " + error.getMessageKey());
        return new Result<>(null, error, errorCode);
    }

    /**
     * @deprecated Use {@link #failure(TranslatableMessage, ErrorCode)} instead.
     * This method is kept for backward compatibility during migration.
     */
    @Deprecated(since = "migration", forRemoval = true)
    public static <T> Result<T> failure(String error, ErrorCode errorCode) {
        logger.error("##### RESULT ERROR ######### " + error);
        return new Result<>(null, new TranslatableMessage("legacy.error", error), errorCode);
    }

    // Méthode pour vérifier si le résultat est un succès
    public boolean isSuccess() {
        return errorCode == null;
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    @SafeVarargs
    public static Result<String> combine(Result<String>... results) {
        for (Result<String> result : results) {
            if (result.isFailure()) {
                return Result.failure(result.getError(), ErrorCode.BAD_REQUEST);
            }
        }
        return Result.success("combine OK");
    }

    // Méthode pour obtenir la valeur si le résultat est un succès
    public T getValue() {
        if (isFailure()) {
            throw new IllegalStateException("Cannot get value from a failed result");
        }
        return value;
    }

    /**
     * Returns the TranslatableMessage error.
     * @return the translatable error message
     * @throws IllegalStateException if called on a successful result
     */
    public TranslatableMessage getError() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get error from a successful result");
        }
        return this.errors;
    }

    /**
     * Resolves the translatable error message to a localized string.
     * @param messageSource the message source to use for resolution
     * @param locale the locale to resolve the message in
     * @return the resolved error message
     * @throws IllegalStateException if called on a successful result
     */
    public String getTranslatedError(MessageSource messageSource, Locale locale) {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get error from a successful result");
        }
        if (errors == null) {
            return null;
        }
        return messageSource.getMessage(errors.getMessageKey(), errors.getParams(), locale);
    }

    /**
     * @deprecated Use {@link #getTranslatedError(MessageSource, Locale)} instead.
     * This method is kept for backward compatibility during migration.
     */
    public String getErrorMessage() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get error message from a successful result");
        }
        return this.errors != null ? this.errors.toString() : null;
    }

    public ErrorCode getErrorCode() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get error from a failed result");
        }
        return this.errorCode;
    }

    @Override
    public String toString() {
        if (isSuccess()) {
            return "Result{value=" + value + "}";
        } else {
            return "Result{error=" + this.errors + ", errorCode=" + errorCode + "}";
        }
    }
}
