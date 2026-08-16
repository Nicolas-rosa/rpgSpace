package com.rpgspace.modules.auth.application;

import com.rpgspace.modules.auth.config.JwtProperties;
import com.rpgspace.modules.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final JwtProperties jwtProperties;

    public String generateAccessToken(User user) {
        return buildToken(user, TYPE_ACCESS, jwtProperties.accessExpiration());
    }

    public String generateRefreshToken(User user, long ttlMillis) {
        return buildToken(user, TYPE_REFRESH, ttlMillis);
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
    }

    public String extractSubject(String token) {
        return parse(token).getSubject();
    }

    public long getAccessExpiration() {
        return jwtProperties.accessExpiration();
    }

    private String buildToken(User user, String type, long ttlMillis) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + ttlMillis);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("type", type)
                .claim("ver", user.getTokenVersion())
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(signingKey())
                .compact();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret()));
    }
}