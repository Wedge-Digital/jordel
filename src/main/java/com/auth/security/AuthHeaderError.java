package com.auth.security;
import com.auth.services.refactored_errors.Error;

// Exemple d'enum pour les erreurs métier
public class AuthHeaderError {
    public static final Error EMPTY_OR_NULL_AUHTHEADER = new Error("AUTH_HEADER_001", "error.auth.header.emptyornull");
    public static final Error INVALID_AUTHHEADER = new Error("AUTH_HEADER_001", "error.auth.header.invalid");
}
