package com.bloodbowlclub.lib.services.result;

import com.bloodbowlclub.lib.services.result.exceptions.ResultException;
import com.bloodbowlclub.lib.web.ApiResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
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
     * Converts a ResultMap to a ResponseEntity.
     * On failure, resolves all TranslatableMessage objects to localized strings using the current locale.
     *
     * @param toConvert the ResultMap to convert
     * @return ResponseEntity with the result value
     * @throws ResultException if the result is a failure
     */
    public ResponseEntity<ApiResponse<K>> toResponse(ResultMap<K> toConvert) throws ResultException {
        if (toConvert.isSuccess()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(toConvert.getValue()));
        }

        // Résoudre le message translatable en string localisée
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, String> translatedErrorMap = toConvert.getTranslatedErrorMap(messageSource, locale);

        // Mapper l'ErrorCode au statut HTTP approprié
        HttpStatus status = mapErrorCodeToHttpStatus(toConvert.getErrorCode());

        return ResponseEntity
                .status(status)
                .body(ApiResponse.failure(translatedErrorMap));
    }

    private HttpStatus mapErrorCodeToHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case UNAUTHORIZED, INVALID_CREDENTIALS, INVALID_TOKEN, EXPIRED_TOKEN -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN, PERMISSION_DENIED -> HttpStatus.FORBIDDEN;
            case ALREADY_EXISTS, CONFLICT -> HttpStatus.CONFLICT;
            case UNPROCESSABLE_ENTITY -> HttpStatus.UNPROCESSABLE_ENTITY;
            case INTERNAL_ERROR, UNKNOWN_ERROR, UNKNOWN_ERROR_CODE -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}