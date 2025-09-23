package com.auth.services.refactored_errors;

// Exemple d'enum pour les erreurs métier
public class AuthError {
    public static final Error USER_NOT_FOUND = new Error("BUS_001", "error.business.user.notfound");
    public static final Error INSUFFICIENT_PERMISSIONS = new Error("BUS_002", "error.business.permissions");
    public static final Error ACCOUNT_LOCKED = new Error("BUS_003", "error.business.account.locked");
    public static final Error OPERATION_NOT_ALLOWED = new Error("BUS_004", "error.business.operation.notallowed");
}
