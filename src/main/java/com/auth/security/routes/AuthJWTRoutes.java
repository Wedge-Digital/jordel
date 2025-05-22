package com.auth.security.routes;

public class AuthJWTRoutes {
    private AuthJWTRoutes() {}
    public static String[] list() {
        return new String[]{"/auth/me"};
    }
}
