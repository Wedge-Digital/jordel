package com.auth.domain;

public class Email {

    @jakarta.validation.constraints.Email
    private final String value;

    public Email(String email) {
        this.value = email;
    }

    @Override
    public String toString() {
        return value;
    }
}
