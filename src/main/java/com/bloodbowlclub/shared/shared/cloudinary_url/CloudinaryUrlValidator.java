package com.bloodbowlclub.shared.shared.cloudinary_url;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class CloudinaryUrlValidator implements ConstraintValidator<CloudinaryUrlConstraint, String> {

    private static final Pattern CLOUDINARY_PATTERN = Pattern.compile(
            "^https?://res\\.cloudinary\\.com/.+",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // ou false si tu veux l'interdire
        }
        return CLOUDINARY_PATTERN.matcher(value).matches();
    }
}

