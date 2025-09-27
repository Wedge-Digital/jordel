package com.auth.security.filters.routes;

public class AionJWTRoutes {

    private AionJWTRoutes() {}

    public static String[] list() {
        return new String[]{
                RoutesDefinition.AION_PACKS
        };
    }
}
