package com.auth.domain.user_account.commands;

public record RegisterCommand(String userId, String username, String email, String password) {
}
