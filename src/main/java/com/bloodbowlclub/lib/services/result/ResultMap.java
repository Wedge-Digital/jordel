package com.bloodbowlclub.lib.services.result;

import com.bloodbowlclub.lib.services.TranslatableMessage;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ResultMap<T> {
    private final T value;
    private final Map<String, TranslatableMessage> errorsMap;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ResultMap.class);
    private final ErrorCode errorCode;

    // Constructeur privé pour forcer l'utilisation des méthodes statiques
    private ResultMap(T value, Map<String, TranslatableMessage> errors, ErrorCode errorCode) {
        this.value = value;
        this.errorsMap = errors;
        this.errorCode = errorCode;
    }

    // Méthode statique pour créer un résultat réussi
    public static <T> ResultMap<T> success(T value) {
        return new ResultMap<>(value, null, null);
    }

    // Méthode statique pour créer un résultat échoué avec TranslatableMessage
    public static <T> ResultMap<T> failure(String key, TranslatableMessage error, ErrorCode code) {
        logger.error("##### RESULT ERROR ######### " + error.getMessageKey());
        Map<String, TranslatableMessage> errors = new HashMap<>();
        errors.put(key, error);
        return new ResultMap<>(null, errors, code);
    }

    public static <T> ResultMap<T> failure(Map<String, TranslatableMessage> errors, ErrorCode code) {
        logger.error("##### RESULT ERROR ######### " + errors);
        return new ResultMap<>(null, errors, code);
    }

    /**
     * @deprecated Use {@link #failure(String, TranslatableMessage, ErrorCode)} instead.
     * This method is kept for backward compatibility during migration.
     */
    @Deprecated(since = "migration", forRemoval = true)
    public static <T> ResultMap<T> failure(String key, String error, ErrorCode code) {
        logger.error("##### RESULT ERROR ######### " + error);
        Map<String, TranslatableMessage> errors = new HashMap<>();
        errors.put(key, new TranslatableMessage("legacy.error", error));
        return new ResultMap<>(null, errors, code);
    }

    /**
     * @deprecated Use {@link #failure(Map, ErrorCode)} with TranslatableMessage instead.
     * This method is kept for backward compatibility during migration.
     */
    @Deprecated(since = "migration", forRemoval = true)
    public static <T> ResultMap<T> failureWithStringMap(Map<String, String> errors, ErrorCode code) {
        logger.error("##### RESULT ERROR ######### " + errors);
        Map<String, TranslatableMessage> translatedErrors = errors.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> new TranslatableMessage("legacy.error", e.getValue())
            ));
        return new ResultMap<>(null, translatedErrors, code);
    }

    public boolean isFailure() {
        return this.errorCode != null;
    }

    public boolean isSuccess() {
        return this.errorCode == null;
    }

    public ErrorCode getErrorCode() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get error from a failed result");
        }
        return this.errorCode;
    }

    @SafeVarargs
    public static <T> ResultMap<T> combine(ResultMap<T>... results) {
        HashMap<String, TranslatableMessage> errors = new HashMap<>();
        for (ResultMap<T> result : results) {
            if (result.isFailure()) {
                errors.putAll(result.errorsMap);
            }
        }
        if (errors.isEmpty()) {
            // On suppose que le success prend une valeur par défaut ou null
            return ResultMap.success(null);
        } else {
            return new ResultMap<>(null, errors, ErrorCode.BAD_REQUEST);
        }
    }

    // Méthode pour obtenir la valeur si le résultat est un succès
    public T getValue() {
        if (isFailure()) {
            throw new IllegalStateException("Cannot get value from a failed result");
        }
        return value;
    }

    /**
     * Returns the first error as a TranslatableMessage.
     * @return the first translatable error message
     * @throws IllegalStateException if called on a successful result
     */
    public String getErrorMessage(MessageSource msg) {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get error from a successful result");
        }
        Map.Entry<String, TranslatableMessage> error = errorsMap.entrySet().iterator().next();
        return msg.getMessage(error.getValue().getMessageKey(), error.getValue().getParams(), Locale.getDefault());
    }

    public TranslatableMessage getTranslatableMessage() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get translated message from a failed result");
        }
        return errorsMap.entrySet().iterator().next().getValue();
    }

    /**
     * Returns the map of all translatable error messages.
     * @return a map of field names to translatable error messages
     * @throws IllegalStateException if called on a successful result
     */
    public Map<String, TranslatableMessage> errorMap() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get errors from a successful result");
        }
        return this.errorsMap;
    }

    /**
     * Resolves all translatable error messages to localized strings.
     * @param messageSource the message source to use for resolution
     * @param locale the locale to resolve the messages in
     * @return a map of field names to resolved error messages
     * @throws IllegalStateException if called on a successful result
     */
    public Map<String, String> getTranslatedErrorMap(MessageSource messageSource, Locale locale) {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get errors from a successful result");
        }
        if (errorsMap == null) {
            return Collections.emptyMap();
        }
        return errorsMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> messageSource.getMessage(
                    e.getValue().getMessageKey(),
                    e.getValue().getParams(),
                    locale
                )
            ));
    }

    /**
     * @deprecated Use {@link #getError()} to get TranslatableMessage instead.
     * This method is kept for backward compatibility during migration.
     */
    @Deprecated(since = "migration", forRemoval = true)
    public String getErrorMessage() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get error message from a successful result");
        }
        Map.Entry<String, TranslatableMessage> error = errorsMap.entrySet().iterator().next();
        return error.getKey() + ":" + error.getValue().toString();
    }

    @Override
    public String toString() {
        if (isSuccess()) {
            return "ResultMap{value=" + value + "}";
        } else {
            return "ResultMap{errors=" + this.errorsMap + ", errorCode=" + errorCode + "}";
        }
    }


}
