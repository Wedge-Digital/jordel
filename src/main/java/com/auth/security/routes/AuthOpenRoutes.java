package com.auth.security.routes;

public class AuthOpenRoutes {
    private AuthOpenRoutes() {}
    public static String[] list() {
        return new String[]{"/auth/refresh", "/auth/login", "/auth/request-otp"};
    }
}
