package com.auth.domain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncryptedPassword {
    private final String value;

    private EncryptedPassword(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean matches(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        return encoder.matches(password, value);
    }
}

