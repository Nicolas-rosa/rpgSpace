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

    private final JwtProperties jwtProperties;

    public String generate(User user) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + jwtProperties.expiration());

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(signingKey())
                .compact();
    }

    public String extractSubject(String token) {
        return claims(token).getSubject();
    }

    public long getExpiration() {
        return jwtProperties.expiration();
    }

    private Claims claims(String token) {
        return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret()));
    }
}
