package com.auth.services;

import com.auth.services.errors.Error;

import java.util.Vector;

public class Result<T> {
    private final T value;
    private final Vector<Error> errors;
    private final boolean isSuccess;

    // Constructeur privé pour forcer l'utilisation des méthodes statiques
    private Result(T value, Vector<Error> errors, boolean isSuccess) {
        this.value = value;
        this.errors = errors;
        this.isSuccess = isSuccess;
    }

    // Méthode statique pour créer un résultat réussi
    public static <T> Result<T> success(T value) {
        return new Result<>(value, null, true);
    }

    // Méthode statique pour créer un résultat échoué
    public static <T> Result<T> failure(Error error) {
        Vector<Error> errors = new Vector<>();
        errors.add(error);
        return new Result<>(null, errors, false);
    }

    public static <T> Result<T> failure(Vector<Error> errors) {
        return new Result<>(null, errors, false);
    }

    // Méthode pour vérifier si le résultat est un succès
    public boolean isSuccess() {
        return isSuccess;
    }

    public boolean isFailure() {
        return !isSuccess;
    }

    // Méthode pour obtenir la valeur si le résultat est un succès
    public T getValue() {
        if (!isSuccess) {
            throw new IllegalStateException("Cannot get value from a failed result");
        }
        return value;
    }

    public Error getError() {
        if (isSuccess) {
            throw new IllegalStateException("Cannot get error from a successful result");
        }
        return this.errors.firstElement();
    }

    public Vector<Error> listErrors() {
        if (isSuccess) {
            throw new IllegalStateException("Cannot get errors from a successful result");
        }
        return this.errors;
    }

    // Méthode pour obtenir le message d'erreur si le résultat est un échec
    public String getErrorMessage() {
        if (isSuccess) {
            throw new IllegalStateException("Cannot get error message from a successful result");
        }
        return this.getError().getMessage();
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
