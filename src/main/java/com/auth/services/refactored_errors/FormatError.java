package com.auth.services.refactored_errors;

// Exemple d'enum pour les erreurs de validation
public class FormatError  {
    public static final Error REQUIRED_FIELD = new Error("VAL_001", "error.validation.required");
    public static final Error INVALID_FORMAT = new Error("VAL_002", "error.validation.format");
    public static final Error VALUE_TOO_LONG = new Error("VAL_003", "error.validation.length.max");
    public static final Error VALUE_TOO_SHORT = new Error("VAL_004", "error.validation.length.min");
    public static final Error INVALID_RANGE = new Error("VAL_005", "error.validation.range");
}
