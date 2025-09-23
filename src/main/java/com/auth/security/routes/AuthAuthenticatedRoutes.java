package com.auth.security.routes;

public class AuthAuthenticatedRoutes {
    public static final String AUTH_ME = "/auth/me";

    private AuthAuthenticatedRoutes() {}

    public static String[] list() {
        return new String[]{AUTH_ME};
    }
}
