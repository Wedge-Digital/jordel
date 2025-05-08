package com.auth.jwt;

import com.auth.services.Result;

public class JwtValidationResult {

    private final boolean isSuccess;
    private final JwtError error;

    private JwtValidationResult(JwtError error, boolean isSuccess) {
        this.error = error;
        this.isSuccess = isSuccess;
    }

    public static JwtValidationResult success() {
        return new JwtValidationResult( null, true);
    }

    // Méthode statique pour créer un résultat échoué
    public static JwtValidationResult failure(JwtError error) {
        return new JwtValidationResult(error, false);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public JwtError getError() {
        return error;
    }

}
