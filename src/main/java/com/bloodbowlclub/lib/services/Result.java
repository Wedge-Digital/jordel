package com.bloodbowlclub.lib.services;

public class Result<T> {
    private final T value;
    private final String errors;
    private final boolean isSuccess;

    // Constructeur privé pour forcer l'utilisation des méthodes statiques
    private Result(T value, String errors, boolean isSuccess) {
        this.value = value;
        this.errors = errors;
        this.isSuccess = isSuccess;
    }

    // Méthode statique pour créer un résultat réussi
    public static <T> Result<T> success(T value) {
        return new Result<>(value, null, true);
    }

    // Méthode statique pour créer un résultat échoué
    public static <T> Result<T> failure(String error) {
        return new Result<>(null, error, false);
    }

    // Méthode pour vérifier si le résultat est un succès
    public boolean isSuccess() {
        return isSuccess;
    }

    public boolean isFailure() {
        return !isSuccess;
    }

    @SafeVarargs
    public static Result<String> combine(Result<String>... results) {
        for (Result<String> result : results) {
            if (result.isFailure()) {
                return Result.failure(result.getError());
            }
        }
        return Result.success("combine OK");
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
        return this.errors;
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
