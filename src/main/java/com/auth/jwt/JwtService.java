package com.auth.jwt;

import com.td.aion.utils.Result;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
    public String generateToken(String userId, Date issuedAt, Date expiresAt) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate JWT token
    public Result<String> validateJwtToken(String token) {
        return this.validateWithKey(token, key);
    }

    private Result<String> execeptionToFailure(Exception e) {
        switch (e.getClass().getName()) {
            case "java.lang.SecurityException":
                return Result.failure(JwtError.INVALID_SIGNATURE.getMessage());
            case "io.jsonwebtoken.MalformedJwtException":
                return Result.failure(JwtError.INVALID_TOKEN.getMessage());
            case "io.jsonwebtoken.ExpiredJwtException":
                return Result.failure(JwtError.EXPIRED_TOKEN.getMessage());
            case "io.jsonwebtoken.UnsupportedJwtException":
                return Result.failure(JwtError.UNSUPPORTED_TOKEN.getMessage());
            case "java.lang.IllegalArgumentException":
                return Result.failure(JwtError.EMPTY_CLAIMS_STRING.getMessage());
            default:
                return Result.failure(JwtError.INVALID_TOKEN.getMessage());
        }
    }

    public Result<String> validateWithKey(String token, SecretKey key) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return Result.success(token);
        } catch (Exception e) {
            return execeptionToFailure(e);
        }
    }

}
