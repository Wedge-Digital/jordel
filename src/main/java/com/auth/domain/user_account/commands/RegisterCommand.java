package com.auth.domain.user_account.commands;

public class RegisterCommand {
    private final String userId;
   private final String  username;
   private final String  email;
   private final String password;

   public RegisterCommand(String userId, String username, String email, String password) {
       this.userId = userId;
       this.username = username;
       this.email = email;
       this.password = password;
   }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }
}
