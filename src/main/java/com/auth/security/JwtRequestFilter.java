package com.auth.security;

import com.auth.jwt.JwtService;
import com.auth.jwt.JwtValidationResult;
import com.auth.security.routes.AuthOpenRoutes;
import com.auth.services.Result;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

@Component
@Profile("with-auth")
public class JwtRequestFilter extends AbstractFilter {

    private final JwtService jwtService;

    public JwtRequestFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected String[] getBypassRoutes() {
        return Stream.of(AuthOpenRoutes.list())
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }

    private String extractJwtTokenFromHeader(String header) {
        return header.substring(7);
    }

    private Result<String> checkAuthHeader(String header) {
        if (header == null) {
            return Result.failure(new HeaderError(HeaderError.EMPTY_OR_NULL_HEADER));
        }

        if (!header.startsWith("Bearer ")) {
            return Result.failure(HeaderError.INVALID_HEADER.getMessage());
        }

        return Result.success(header);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        Result<String> headerCheckResult = checkAuthHeader(authorizationHeader);
        if (!headerCheckResult.isSuccess()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing Authorization header");
            return;
        }

        String jwtToken = extractJwtTokenFromHeader(authorizationHeader);
        JwtValidationResult validationResult = jwtService.validateJwtToken(jwtToken);

        if (!validationResult.isSuccess()) {
            switch (validationResult.getError()) {
                case INVALID_TOKEN, INVALID_SIGNATURE, UNSUPPORTED_TOKEN, EMPTY_CLAIMS_STRING ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                case EXPIRED_TOKEN ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired JWT Token");
                default -> response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossible to parse JWT token");
            }
            return;
        }

        String userPhone = jwtService.getUsernameFromToken(jwtToken);

        if (userPhone == null || userPhone.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unknown or inactive user");
            return;
        }

        UsernamePasswordAuthenticationToken springAuthToken = new UsernamePasswordAuthenticationToken(
                userResult.getValue(), null, null);

        springAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(springAuthToken);

        chain.doFilter(request, response);
    }
}
