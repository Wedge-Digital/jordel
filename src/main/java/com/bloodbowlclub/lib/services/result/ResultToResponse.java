package com.bloodbowlclub.lib.services.result;

import com.bloodbowlclub.lib.services.result.exceptions.ResultException;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResultToResponse<K> {
    public ResponseEntity<K> toResponse(Result<K> toConvert) throws ResultException {
        if (toConvert.isSuccess()) {
            return ResponseEntity.ok(toConvert.getValue());
        }

        ErrorCode errorCode = toConvert.getErrorCode();
        HashMap<String, String> errors = new HashMap<>();
        errors.put("errors", toConvert.getErrorMessage());
        // Amélioration: instanciation correcte de l'exception avec le constructeur (Map<String,String>)
        Class<? extends ResultException> exClass = ErrorToException.get(errorCode);
        if (exClass == null) {
            // Fallback si aucun mapping n'est défini pour ce code d'erreur
            throw new ResultException(errors);
        }
        try {
            java.lang.reflect.Constructor<? extends ResultException> ctor = exClass.getDeclaredConstructor(Map.class);
            throw ctor.newInstance(errors);
        } catch (ResultException e) {
            throw e;
        } catch (ReflectiveOperationException e) {
            // Fallback si la réflexion échoue pour une raison quelconque
            throw new ResultException(errors);
        }
    }

    public ResponseEntity<K> toResponse(ResultMap<K> toConvert) throws ResultException {
        if (toConvert.isSuccess()) {
            return ResponseEntity.ok(toConvert.getValue());
        }

        ErrorCode errorCode = toConvert.getErrorCode();
        // Amélioration: instanciation correcte de l'exception avec le constructeur (Map<String,String>)
        Class<? extends ResultException> exClass = ErrorToException.get(errorCode);
        if (exClass == null) {
            // Fallback si aucun mapping n'est défini pour ce code d'erreur
            throw new ResultException(toConvert.errorMap());
        }
        try {
            java.lang.reflect.Constructor<? extends ResultException> ctor = exClass.getDeclaredConstructor(Map.class);
            throw ctor.newInstance(toConvert.errorMap());
        } catch (ResultException e) {
            throw e;
        } catch (ReflectiveOperationException e) {
            // Fallback si la réflexion échoue pour une raison quelconque
            throw new ResultException(toConvert.errorMap());
        }
    }
}