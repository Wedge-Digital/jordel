package com.bloodbowlclub.auth.io.security.filters;

import com.bloodbowlclub.auth.domain.user_account.ActiveUserAccount;
import com.bloodbowlclub.auth.domain.user_account.values.UserRole;
import com.bloodbowlclub.auth.io.security.routes.AuthOpenRoutes;
import com.bloodbowlclub.auth.io.services.AbstractAuthService;
import com.bloodbowlclub.auth.io.services.JwtService;
import com.bloodbowlclub.lib.auth.AbstractFilter;
import com.bloodbowlclub.lib.services.Result;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class JwtRequestFilter extends AbstractFilter {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    private final AbstractAuthService authService;

    private final MessageSource messageSource;

    private final JwtService jwtService;

    public JwtRequestFilter(AbstractAuthService authService,
                            MessageSource messageSource,
                            JwtService jwtService) {
        this.authService = authService;
        this.messageSource = messageSource;
        this.jwtService = jwtService;
    }

    @Override
    protected String[] getBypassRoutes() {
        return AuthOpenRoutes.list();
    }

    private String extractJwtTokenFromHeader(String header) {
        return header.substring(7);
    }

    private Result<String> checkAuthHeader(String header) {
        if (header == null) {
            String errorMessage = messageSource.getMessage("http.header.missing", null, Locale.getDefault());
            return Result.failure(errorMessage);
        }

        if (!header.startsWith("Bearer ")) {
            String errorMessage = messageSource.getMessage("user_account.bad_format", null, Locale.getDefault());
            return Result.failure(errorMessage);
        }

        return Result.success(header);
    }

    private List<GrantedAuthority> buildAuthorities(List<UserRole> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toList());
    }

    private void registerUserInContext(ActiveUserAccount loggedUser, HttpServletRequest request) {
        List<GrantedAuthority> updatedAuthorities = buildAuthorities(loggedUser.getRoles());
        logger.info(updatedAuthorities.toString());

        UsernamePasswordAuthenticationToken springAuthToken = new UsernamePasswordAuthenticationToken(
                loggedUser, null, updatedAuthorities);

        // and carry on the filter chain
        springAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(springAuthToken);
        logger.debug(loggedUser.toString());
        logger.debug(updatedAuthorities.toString());
        logger.debug("=========== user added to context!!!!!!");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", authorizationHeader);

        Result<String> headerCheckResult = checkAuthHeader(authorizationHeader);
        if (!headerCheckResult.isSuccess()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing Authorization header");
            logger.warn("Authorization header: {}", headerCheckResult);
            return;
        }

        String jwtToken = extractJwtTokenFromHeader(authorizationHeader);
        logger.debug("JWT token: {}", jwtToken);

        // validate Jwt
        Result<String> jwtValidation = jwtService.validateJwtToken(jwtToken);

        if (jwtValidation.isFailure() ) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, jwtValidation.getErrorMessage());
            return;
        }

        // extract username from Jwt
        String username = jwtService.getUsernameFromToken(jwtToken);

        logger.debug("Checking if user exists: {}", username);

        // if validation succeed, check if user is locally knonw
        Result<ActiveUserAccount> searchForLocalUser = authService.isUserIsKnownAndActive(username);
        logger.debug("Search for local user: {}", searchForLocalUser);

        if (searchForLocalUser.isFailure()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, searchForLocalUser.getErrorMessage());
            return;
        }

        ActiveUserAccount foundLocalUser = searchForLocalUser.getValue();

        registerUserInContext(foundLocalUser, request);
        chain.doFilter(request, response);
    }
}
