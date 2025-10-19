package com.lib.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Locale;

public class ULIDValidator implements ConstraintValidator<ULIDConstraint, String> {

    @Override
    public void initialize(ULIDConstraint constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Treat null or empty as invalid here so that specific @NotNull/@NotEmpty constraints can also report
        if (value == null || value.isEmpty()) {
            return false;
        }
        String trimmed = value.trim();
        if (trimmed.length() != 26) {
            return false;
        }
        String upper = trimmed.toUpperCase(Locale.ROOT);
        // Crockford's Base32 without I, L, O, U
        return upper.matches("^[0-9A-HJ-NP-TV-Z]{26}$");
    }
}
