package com.bloodbowlclub.auth.io.services;

import com.bloodbowlclub.auth.io.web.JwtTokensResponse;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Component
public class JwtService {


    private final String jwtSecret;


    private final int accessExpirationOffset;


    private final long refreshExpirationOffset;

    private SecretKey key;

    private final MessageSource messageSource;

    public JwtService(@Value("${jwt.secret}") String jwtSecret,
                      @Value("${jwt.expiration.access}") int accessExpirationOffset,
                      @Value("${jwt.expiration.refresh}") long refreshExpirationOffset,
                      MessageSource messageSource) {
        this.jwtSecret = jwtSecret;
        this.accessExpirationOffset = accessExpirationOffset;
        this.refreshExpirationOffset = refreshExpirationOffset;
        this.messageSource = messageSource;
    }

    // Initializes the key after the class is instantiated and the jwtSecret is injected,
    // preventing the repeated creation of the key and enhancing performance
    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtTokensResponse buildAuthTokens(String username) {
        Date issuedAt = new Date();
        Date accessExpiresAt = computeAccessExpirationDate(new Date());
        Date refreshExpiresAt = computeRefreshExpirationDate(new Date());
        String accessToken = generateToken(username, issuedAt, accessExpiresAt);
        String refreshToken = generateToken(username, issuedAt, refreshExpiresAt);
        return new JwtTokensResponse(accessToken, refreshToken);
    }

    public Date computeAccessExpirationDate(Date issuedAt) {
        return new Date(issuedAt.getTime() + accessExpirationOffset);
    }

    public Date computeRefreshExpirationDate(Date issuedAt) {
        return new Date(issuedAt.getTime() + refreshExpirationOffset);
    }

    public int getAccessExpiration() {
        return accessExpirationOffset;
    }

    public long getRefreshExpiration() {
        return refreshExpirationOffset;
    }


    // Generate JWT token
    public String generateToken(String username, Date issuedAt, Date expiresAt) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(key)
                .compact();
    }
    // Get username from JWT token
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String renewAccessToken(String token) {
        String username = getUsernameFromToken(token);
        Date issuedAt = new Date();
        Date expiresAt = computeAccessExpirationDate(new Date());
        return generateToken(username, issuedAt, expiresAt);
    }

    public String extendRefreshToken(String token) {
        String username = getUsernameFromToken(token);
        Date issuedAt = new Date();
        Date expiresAt = computeRefreshExpirationDate(new Date());
        return generateToken(username, issuedAt, expiresAt);
    }

    // Validate JWT token
    public Result<String> validateJwtToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return Result.success("no pb");
        } catch (SecurityException e) {
            String errorMessage = messageSource.getMessage("jwt.invalid_token", null, LocaleContextHolder.getLocale());
            return Result.failure(errorMessage, ErrorCode.UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            String errorMessage = messageSource.getMessage("jwt.invalid_token", null, LocaleContextHolder.getLocale());
            return Result.failure(errorMessage, ErrorCode.UNAUTHORIZED);
        } catch (ExpiredJwtException e) {
            String errorMessage = messageSource.getMessage("jwt.expired_token", null, LocaleContextHolder.getLocale());
            return Result.failure(errorMessage, ErrorCode.UNAUTHORIZED);
        } catch (UnsupportedJwtException e) {
            String errorMessage = messageSource.getMessage("jwt.unknown_jwt_token", null, LocaleContextHolder.getLocale());
            return Result.failure(errorMessage, ErrorCode.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            String errorMessage = messageSource.getMessage("jwt.invalid_token", null, LocaleContextHolder.getLocale());
            return Result.failure(errorMessage, ErrorCode.UNAUTHORIZED);
        }
    }

}
