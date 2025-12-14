package com.bloodbowlclub.lib.services.result;

import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ResultMap<T> {
    private final T value;
    private final Map<String, String> errorsMap;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ResultMap.class);
    private final ErrorCode errorCode;

    // Constructeur privé pour forcer l'utilisation des méthodes statiques
    private ResultMap(T value, Map<String, String> errors, ErrorCode errorCode) {
        this.value = value;
        this.errorsMap = errors;
        this.errorCode = errorCode;
    }

    // Méthode statique pour créer un résultat réussi
    public static <T> ResultMap<T> success(T value) {
        return new ResultMap<>(value, null, null);
    }

    // Méthode statique pour créer un résultat échoué
    public static <T> ResultMap<T> failure(String key, String error, ErrorCode code) {
        logger.error("##### RESULT ERROR ######### " + error);
        Map<String, String> errors = new HashMap<>();
        errors.put(key, error);
        return new ResultMap<>(null, errors, code);
    }

    public static <T> ResultMap<T> failure(Map<String, String> errors, ErrorCode code) {
        logger.error("##### RESULT ERROR ######### " + errors);
        return new ResultMap<>(null, errors, code);
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
        HashMap<String, String> errors = new HashMap<>();
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

    public String getError() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get error from a successful result");
        }
        Map.Entry<String, String> error = errorsMap.entrySet().iterator().next();
        return error.getKey()+":"+error.getValue();
    }

    public Map<String, String> errorMap() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get errors from a successful result");
        }
        return this.errorsMap;
    }

    // Méthode pour obtenir le message d'erreur si le résultat est un échec
    public String getErrorMessage() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get error message from a successful result");
        }
        return this.getError();
    }

    @Override
    public String toString() {
        if (isSuccess()) {
            return "Result{value=" + value + "}";
        } else {
            return "Result{errorMessage='" + this.getErrorMessage() + "'}";
        }
    }


}
