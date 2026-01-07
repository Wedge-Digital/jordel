package com.bloodbowlclub.lib.services.result;

import com.bloodbowlclub.lib.services.result.exceptions.ResultException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Converts Result or ResultMap objects to HTTP ResponseEntity.
 * This class resolves TranslatableMessage objects to localized strings
 * before creating the response.
 */
@Component
public class ResultToResponse<K> {

    private final MessageSource messageSource;

    public ResultToResponse(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Converts a Result to a ResponseEntity.
     * On failure, resolves the TranslatableMessage to a localized string using the current locale.
     *
     * @param toConvert the Result to convert
     * @return ResponseEntity with the result value
     * @throws ResultException if the result is a failure
     */
    public ResponseEntity<K> toResponse(Result<K> toConvert) throws ResultException {
        if (toConvert.isSuccess()) {
            return ResponseEntity.ok(toConvert.getValue());
        }

        ErrorCode errorCode = toConvert.getErrorCode();
        Locale locale = LocaleContextHolder.getLocale();

        // Resolve the TranslatableMessage to a localized string
        String resolvedError = toConvert.getTranslatedError(messageSource, locale);

        HashMap<String, String> errors = new HashMap<>();
        errors.put("errors", resolvedError);

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

    /**
     * Converts a ResultMap to a ResponseEntity.
     * On failure, resolves all TranslatableMessage objects to localized strings using the current locale.
     *
     * @param toConvert the ResultMap to convert
     * @return ResponseEntity with the result value
     * @throws ResultException if the result is a failure
     */
    public ResponseEntity<K> toResponse(ResultMap<K> toConvert) throws ResultException {
        if (toConvert.isSuccess()) {
            return ResponseEntity.ok(toConvert.getValue());
        }

        ErrorCode errorCode = toConvert.getErrorCode();
        Locale locale = LocaleContextHolder.getLocale();

        // Resolve all TranslatableMessage objects to localized strings
        Map<String, String> resolvedErrors = toConvert.getTranslatedErrorMap(messageSource, locale);

        // Amélioration: instanciation correcte de l'exception avec le constructeur (Map<String,String>)
        Class<? extends ResultException> exClass = ErrorToException.get(errorCode);
        if (exClass == null) {
            // Fallback si aucun mapping n'est défini pour ce code d'erreur
            throw new ResultException(resolvedErrors);
        }
        try {
            java.lang.reflect.Constructor<? extends ResultException> ctor = exClass.getDeclaredConstructor(Map.class);
            throw ctor.newInstance(resolvedErrors);
        } catch (ResultException e) {
            throw e;
        } catch (ReflectiveOperationException e) {
            // Fallback si la réflexion échoue pour une raison quelconque
            throw new ResultException(resolvedErrors);
        }
    }
}