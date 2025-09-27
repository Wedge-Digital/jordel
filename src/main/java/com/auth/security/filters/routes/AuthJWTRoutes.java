package com.auth.security.filters.routes;

public class AuthJWTRoutes {
    private AuthJWTRoutes() {}
    public static String[] list() {
        return new String[]{
                RoutesDefinition.AUTH_ME
        };
    }
}
