package com.auth.jwt;
import com.auth.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    private static final String[] excludedEndpoints = new String[] {"/refresh", "/login" };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Arrays.stream(excludedEndpoints).anyMatch(e -> new AntPathMatcher().match(e, request.getPathInfo()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing Authorization header");
            return;
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed authorization Header");
            return;
        }

        String jwt = authorizationHeader.substring(7);
        JwtValidationResult validationResult = jwtService.validateJwtToken(jwt);

        if (!validationResult.isSuccess()) {
            switch (validationResult.getError()) {
                case INVALID_TOKEN, INVALID_SIGNATURE, UNSUPPORTED_TOKEN, EMPTY_CLAIMS_STRING ->
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token");
                case EXPIRED_TOKEN ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired JWT Token");
                default -> response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossible to parse JWT token");
            }
            return;
        }

        String username = jwtService.getUsernameFromToken(jwt);

        UserDetails userDetails;
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                userDetails = this.userService.loadUserByUsername(username);
            } catch (UsernameNotFoundException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unknown user");
                return;
            }

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            chain.doFilter(request, response);
        }
    }
}
