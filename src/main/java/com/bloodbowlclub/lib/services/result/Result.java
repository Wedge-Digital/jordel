package com.bloodbowlclub.lib.services.result;

import org.slf4j.LoggerFactory;

public class Result<T> {
    private final T value;
    private final String errors;
    private final ErrorCode errorCode;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Result.class);

    // Constructeur privé pour forcer l'utilisation des méthodes statiques
    private Result(T value, String errors, ErrorCode errorCode) {
        this.value = value;
        this.errors = errors;
        this.errorCode = errorCode;
    }

    // Méthode statique pour créer un résultat réussi
    public static <T> Result<T> success(T value) {
        return new Result<>(value, null, null);
    }

    // Méthode statique pour créer un résultat échoué
    public static <T> Result<T> failure(String error,  ErrorCode errorCode) {
        logger.error("##### RESULT ERROR ######### " + error);
        return new Result<>(null, error, errorCode);
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

    public String getError() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get error from a successful result");
        }
        return this.errors;
    }

    public ErrorCode getErrorCode() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get error from a failed result");
        }
        return this.errorCode;
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
