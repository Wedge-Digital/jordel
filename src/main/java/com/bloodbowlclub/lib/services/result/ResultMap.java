package com.bloodbowlclub.lib.services.result;

import com.bloodbowlclub.lib.services.result.exceptions.ResultException;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResultMap<T> {
    private final T value;
    private final Map<String, String> errorsMap;
    // Méthode pour vérifier si le résultat est un succès
    @Getter
    private final boolean isSuccess;

    private final ErrorCode errorCode;

    // Constructeur privé pour forcer l'utilisation des méthodes statiques
    private ResultMap(T value, Map<String, String> errors, boolean isSuccess, ErrorCode errorCode) {
        this.value = value;
        this.errorsMap = errors;
        this.isSuccess = isSuccess;
        this.errorCode = errorCode;
    }

    // Méthode statique pour créer un résultat réussi
    public static <T> ResultMap<T> success(T value) {
        return new ResultMap<>(value, null, true, null);
    }

    // Méthode statique pour créer un résultat échoué
    public static <T> ResultMap<T> failure(String key, String error, ErrorCode code) {
        Map<String, String> errors = new HashMap<>();
        errors.put(key, error);
        return new ResultMap<>(null, errors, false, code);
    }

    public static <T> ResultMap<T> failure(Map<String, String> errors, ErrorCode code) {
        return new ResultMap<>(null, errors, false, code);
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
            return new ResultMap<>(null, errors, false, ErrorCode.BAD_REQUEST);
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

    public Map<String, String> errorMap() {
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

    public ResponseEntity<Map<String,String>> toResponse() throws ResultException {
        HashMap<String, String> res = new HashMap<>();
        if (isSuccess()) {
            res.put("result", "success");
            return ResponseEntity.ok(res);
        }
        // Amélioration: instanciation correcte de l'exception avec le constructeur (Map<String,String>)
        Class<? extends ResultException> exClass = ErrorToException.get(errorCode);
        if (exClass == null) {
            // Fallback si aucun mapping n'est défini pour ce code d'erreur
            throw new ResultException(this.errorsMap);
        }
        try {
            java.lang.reflect.Constructor<? extends ResultException> ctor = exClass.getDeclaredConstructor(Map.class);
            throw ctor.newInstance(this.errorsMap);
        } catch (ResultException e) {
            throw e;
        } catch (ReflectiveOperationException e) {
            // Fallback si la réflexion échoue pour une raison quelconque
            throw new ResultException(this.errorsMap);
        }
    }
}
