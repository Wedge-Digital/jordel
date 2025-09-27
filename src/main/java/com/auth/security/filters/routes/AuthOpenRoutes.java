package com.auth.security.filters.routes;

public class AuthOpenRoutes {
    private AuthOpenRoutes() {}
    public static String[] list() {
        return new String[]{
                RoutesDefinition.AUTH_REFRESH,
                RoutesDefinition.AION_SIDEWALK
        };
    }
}
