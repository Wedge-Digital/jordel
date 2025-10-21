package com.bloodbowlclub.auth.io.security.routes;

public class AuthOpenRoutes {
    public static final String AUTH_REFRESH = "/auth/refresh";
    public static final String AUTH_LOGIN = "/auth/login";


    private AuthOpenRoutes() {}
    public static String[] list() {
        return new String[]{AUTH_REFRESH,AUTH_LOGIN};
    }
}
