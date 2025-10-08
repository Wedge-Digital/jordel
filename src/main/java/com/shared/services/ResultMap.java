package com.shared.services;

import java.util.HashMap;
import java.util.Map;

public class ResultMap<T> {
    private final T value;
    private final Map<String, String> errorsMap;
    private final boolean isSuccess;

    // Constructeur privé pour forcer l'utilisation des méthodes statiques
    private ResultMap(T value, Map<String, String> errors, boolean isSuccess) {
        this.value = value;
        this.errorsMap = errors;
        this.isSuccess = isSuccess;
    }

    // Méthode statique pour créer un résultat réussi
    public static <T> ResultMap<T> success(T value) {
        return new ResultMap<>(value, null, true);
    }

    // Méthode statique pour créer un résultat échoué
    public static <T> ResultMap<T> failure(String key, String error) {
        Map<String, String> errors = new HashMap<>();
        errors.put(key, error);
        return new ResultMap<>(null, errors, false);
    }

    public static <T> ResultMap<T> failure(Map<String, String> errors) {
        return new ResultMap<>(null, errors, false);
    }

    // Méthode pour vérifier si le résultat est un succès
    public boolean isSuccess() {
        return isSuccess;
    }

    public boolean isFailure() {
        return !isSuccess;
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
            return new ResultMap<>(null, errors, false);
        }
    }

    // Méthode pour obtenir la valeur si le résultat est un succès
    public T getValue() {
        if (!isSuccess) {
            throw new IllegalStateException("Cannot get value from a failed result");
        }
        return value;
    }

    public String getError() {
        if (isSuccess) {
            throw new IllegalStateException("Cannot get error from a successful result");
        }
        Map.Entry<String, String> error = errorsMap.entrySet().iterator().next();
        return error.getKey()+":"+error.getValue();
    }

    public Map<String, String> listErrors() {
        if (isSuccess) {
            throw new IllegalStateException("Cannot get errors from a successful result");
        }
        return this.errorsMap;
    }

    // Méthode pour obtenir le message d'erreur si le résultat est un échec
    public String getErrorMessage() {
        if (isSuccess) {
            throw new IllegalStateException("Cannot get error message from a successful result");
        }
        return this.getError();
    }

    @Override
    public String toString() {
        if (isSuccess) {
            return "Result{value=" + value + "}";
        } else {
            return "Result{errorMessage='" + this.getErrorMessage() + "'}";
        }
    }
}
