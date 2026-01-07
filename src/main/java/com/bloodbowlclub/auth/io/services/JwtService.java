package com.bloodbowlclub.auth.io.services;

import com.bloodbowlclub.auth.io.web.JwtTokensResponse;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.TranslatableMessage;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
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

    public JwtService(@Value("${jwt.secret}") String jwtSecret,
                      @Value("${jwt.expiration.access}") int accessExpirationOffset,
                      @Value("${jwt.expiration.refresh}") long refreshExpirationOffset) {
        this.jwtSecret = jwtSecret;
        this.accessExpirationOffset = accessExpirationOffset;
        this.refreshExpirationOffset = refreshExpirationOffset;
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
            return Result.failure(new TranslatableMessage("jwt.invalid_token"), ErrorCode.UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            return Result.failure(new TranslatableMessage("jwt.invalid_token"), ErrorCode.UNAUTHORIZED);
        } catch (ExpiredJwtException e) {
            return Result.failure(new TranslatableMessage("jwt.expired_token"), ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            return Result.failure(new TranslatableMessage("jwt.unknown_jwt_token"), ErrorCode.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            return Result.failure(new TranslatableMessage("jwt.invalid_token"), ErrorCode.UNAUTHORIZED);
        }
    }

}
