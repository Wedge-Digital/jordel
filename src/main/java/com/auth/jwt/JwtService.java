package com.auth.jwt;

import com.auth.models.JwtTokens;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.access}")
    private int accessExpirationOffset;

    @Value("${jwt.expiration.refresh}")
    private long refreshExpirationOffset;

    private SecretKey key;

    // Initializes the key after the class is instantiated and the jwtSecret is injected,
    // preventing the repeated creation of the key and enhancing performance
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public JwtTokens buildAuthTokens(String username) {
        Date issuedAt = new Date();
        Date accessExpiresAt = computeAccessExpirationDate(new Date());
        Date refreshExpiresAt = computeRefreshExpirationDate(new Date());
        String accessToken = generateToken(username, issuedAt, accessExpiresAt);
        String refreshToken = generateToken(username, issuedAt, refreshExpiresAt);
        return new JwtTokens(accessToken, refreshToken);
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
                .setSubject(username)
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    // Get username from JWT token
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
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
    public JwtValidationResult validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return JwtValidationResult.success();
        } catch (SecurityException e) {
            return JwtValidationResult.failure(JwtError.INVALID_SIGNATURE);
        } catch (MalformedJwtException e) {
            return JwtValidationResult.failure(JwtError.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            return JwtValidationResult.failure(JwtError.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            return JwtValidationResult.failure(JwtError.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            return JwtValidationResult.failure(JwtError.EMPTY_CLAIMS_STRING);
        }
    }
}
