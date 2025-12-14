package com.bloodbowlclub.auth.io.security.filters;

import com.bloodbowlclub.auth.domain.user_account.BaseUserAccount;
import com.bloodbowlclub.auth.domain.user_account.values.UserRole;
import com.bloodbowlclub.auth.io.services.JwtService;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final MessageSource messageSource;

    private final JwtService jwtService;
    private final EventStore eventStore;

    public JwtRequestFilter(@Qualifier("eventStore") EventStore eventStore,
                            MessageSource messageSource,
                            JwtService jwtService) {
        this.eventStore = eventStore;
        this.messageSource = messageSource;
        this.jwtService = jwtService;
    }

    private String extractJwtTokenFromHeader(String header) {
        return header.substring(7);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().startsWith("/auth");
    }

    private Result<String> checkAuthHeader(String header) {
        if (header == null) {
            String errorMessage = messageSource.getMessage("http.header.missing", null, Locale.getDefault());
            return Result.failure(errorMessage, ErrorCode.UNAUTHORIZED);
        }

        if (!header.startsWith("Bearer ")) {
            String errorMessage = messageSource.getMessage("user_account.bad_format", null, Locale.getDefault());
            return Result.failure(errorMessage, ErrorCode.UNAUTHORIZED);
        }

        return Result.success(header);
    }

    private List<GrantedAuthority> buildAuthorities(List<UserRole> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toList());
    }

    private void registerUserInContext(BaseUserAccount loggedUser, HttpServletRequest request) {
        List<GrantedAuthority> updatedAuthorities = buildAuthorities(loggedUser.getRoles());
        logger.info(updatedAuthorities.toString());

        UsernamePasswordAuthenticationToken springAuthToken = new UsernamePasswordAuthenticationToken(
                loggedUser, null, updatedAuthorities);

        // and carry on the filter chain
        springAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(springAuthToken);
        logger.info(loggedUser.toString());
        logger.info(updatedAuthorities.toString());
        logger.info("=========== user added to context!!!!!!");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        logger.info("Authorization header: {}", authorizationHeader);

        Result<String> headerCheckResult = checkAuthHeader(authorizationHeader);
        if (!headerCheckResult.isSuccess()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing Authorization header");
            logger.warn("Authorization header: {}", headerCheckResult);
            return;
        }

        String jwtToken = extractJwtTokenFromHeader(authorizationHeader);
        logger.info("JWT token: {}", jwtToken);

        // validate Jwt
        Result<String> jwtValidation = jwtService.validateJwtToken(jwtToken);

        if (jwtValidation.isFailure() ) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, jwtValidation.getErrorMessage());
            return;
        }

        // extract username from Jwt
        String username = jwtService.getUsernameFromToken(jwtToken);

        logger.info("Checking if user exists: {}", username);

        // if validation succeed, check if user is locally knonw
        Result<AggregateRoot> searchForLocalUser = eventStore.findUser(username);
        logger.info("Search for local user: {}", searchForLocalUser);

        if (searchForLocalUser.isFailure()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, searchForLocalUser.getErrorMessage());
            return;
        }

        BaseUserAccount foundLocalUser = (BaseUserAccount) searchForLocalUser.getValue();

        registerUserInContext(foundLocalUser, request);
        chain.doFilter(request, response);
    }
}
