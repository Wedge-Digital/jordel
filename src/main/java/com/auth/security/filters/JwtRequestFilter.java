package com.auth.security.filters;

import com.td.aion.business_domain.id_service.IdServiceInterface;
import com.td.aion.io.repositories.user.UserEntity;
import com.td.aion.io.repositories.user.UserRepository;
import com.td.aion.io.web.auth.controller.UserEntityAcountError;
import com.td.aion.io.web.auth.jwt.AbstractUserChecker;
import com.td.aion.io.web.auth.jwt.KeycloakUser;
import com.td.aion.io.web.auth.security.filters.routes.AuthOpenRoutes;
import com.td.aion.io.web.auth.services.AbstractAuthService;
import com.td.aion.utils.Result;
import com.td.aion.utils.date_service.DateServiceInterface;
import jakarta.persistence.EntityManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Profile("with-auth")
public class JwtRequestFilter extends AbstractFilter {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    private final AbstractUserChecker userChecker;
    private final IdServiceInterface idService;
    private final AbstractAuthService authService;
    private final UserRepository userRepository;
    private final DateServiceInterface dateService;

    public JwtRequestFilter(IdServiceInterface idservice, AbstractAuthService authService, AbstractUserChecker keycloakChecker, UserRepository userRepository, DateServiceInterface dateService, EntityManager entityManager) {
        this.authService = authService;
        this.idService = idservice;
        this.userChecker = keycloakChecker;
        this.userRepository = userRepository;
        this.dateService = dateService;
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
            return Result.failure(HeaderError.EMPTY_OR_NULL_HEADER.getMessage());
        }

        if (!header.startsWith("Bearer ")) {
            return Result.failure(HeaderError.INVALID_HEADER.getMessage());
        }

        return Result.success(header);
    }

    private List<GrantedAuthority> buildAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }

    private void registerUserInContext(UserEntity loggedUser, HttpServletRequest request) {
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

        // validate from keycloak
        Result<KeycloakUser> userInfoExtraction = userChecker.getUserInfosFromToken(jwtToken);

        if (userInfoExtraction == null ) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Extracted user information is unprocessable");
            return;
        }

        // if validation fails, refuse to continue
        if (userInfoExtraction.isFailure()) {
            logger.warn("User info extraction failed: {}", userInfoExtraction.getErrorMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, userInfoExtraction.getErrorMessage());
            return;
        }

        KeycloakUser kcUser = userInfoExtraction.getValue();
        logger.debug("Checking if user exists: {}", kcUser);

        // if validation succeed, check if user is locally knonw
        Result<UserEntity> searchForLocalUser = authService.isUserIsKnownAndActive(kcUser.getUsername());
        logger.debug("Search for local user: {}", searchForLocalUser);

        if (searchForLocalUser.isFailure()) {

            String errorCause = searchForLocalUser.getErrorMessage();
            if (Objects.equals(errorCause, UserEntityAcountError.INACTIVE_ACCOUNT.getMessage())) {
                logger.warn("User {} is already in use", kcUser.getUsername());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorCause);
                return;
            }

            if (Objects.equals(errorCause, UserEntityAcountError.UNEXISTING_ACCOUNT.getMessage())) {
                // if user isn't locally known, register local user
                UserEntity userEntity = kcUser.toNewUserEntity(this.idService.getStringId());
                try {
                    this.userRepository.save(userEntity);
                } catch (DataIntegrityViolationException e) {
                    logger.info(e.getMessage());
                }
                this.registerUserInContext(userEntity, request);
                chain.doFilter(request, response);
                return;
            }
        }

        UserEntity foundLocalUser = searchForLocalUser.getValue();

        // if user is locally known, check if he needs update
        if (foundLocalUser.needsUpdate(kcUser)) {
            // UPDATE DEACTIVATED FOR NOW @TODO
//            this.userRepository.updateUser(
//                    foundLocalUser.getId(),
//                    kcUser.getFirstName(),
//                    kcUser.getLastName(),
//                    kcUser.getExternalId(),
//                    true,
//                    kcUser.getCheckSum(),
//                    Arrays.asList(kcUser.getRoles()));
        } else {
            this.userRepository.updateLastLogin(foundLocalUser.getId(), dateService.now());
        }

        registerUserInContext(foundLocalUser, request);
        chain.doFilter(request, response);
    }
}
