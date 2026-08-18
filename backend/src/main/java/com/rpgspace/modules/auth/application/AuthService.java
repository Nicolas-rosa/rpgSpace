package com.rpgspace.modules.auth.application;

import com.rpgspace.modules.auth.api.dto.AuthResponse;
import com.rpgspace.modules.auth.api.dto.LoginRequest;
import com.rpgspace.modules.auth.api.dto.RefreshTokenRequest;
import com.rpgspace.modules.auth.api.dto.RegisterRequest;
import com.rpgspace.modules.auth.api.dto.UserResponse;
import com.rpgspace.modules.auth.api.mapper.UserMapper;
import com.rpgspace.modules.auth.config.JwtProperties;
import com.rpgspace.modules.auth.domain.RefreshToken;
import com.rpgspace.modules.auth.infrastructure.persistence.RefreshTokenRepository;
import com.rpgspace.modules.user.application.UserService;
import com.rpgspace.modules.user.domain.User;
import com.rpgspace.shared.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        User user = userService.create(request.username(), request.email(), passwordEncoder.encode(request.password()));
        return authenticate(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userService.requireEnabledByEmail(request.email());
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Credenciais inválidas.");
        }
        return authenticate(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String tokenHash = hash(request.refreshToken());
        RefreshToken stored = refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Sessão inválida ou expirada."));

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            stored.revoke();
            refreshTokenRepository.save(stored);
            throw new UnauthorizedException("Sessão expirada. Faça login novamente.");
        }

        User user = stored.getUser();
        if (!user.isEnabled()) {
            throw new UnauthorizedException("Conta desativada.");
        }

        try {
            Claims claims = jwtService.parse(request.refreshToken());
            if (!JwtService.TYPE_REFRESH.equals(claims.get("type", String.class))) {
                throw new UnauthorizedException("Token inválido.");
            }
            long tokenVersion = claims.get("ver", Long.class);
            if (tokenVersion != user.getTokenVersion()) {
                throw new UnauthorizedException("Sessão revogada. Faça login novamente.");
            }
        } catch (JwtException | IllegalArgumentException exception) {
            throw new UnauthorizedException("Sessão inválida ou expirada.");
        }

        stored.revoke();
        refreshTokenRepository.save(stored);
        return authenticate(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        refreshTokenRepository.findByTokenHashAndRevokedFalse(hash(refreshToken))
                .ifPresent(stored -> {
                    stored.revoke();
                    refreshTokenRepository.save(stored);
                });
    }

    @Transactional(readOnly = true)
    public UserResponse me(String email) {
        return userMapper.toResponse(userService.requireEnabledByEmail(email));
    }

    private AuthResponse authenticate(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, jwtProperties.refreshExpiration());
        Instant refreshExpiresAt = Instant.now().plusMillis(jwtProperties.refreshExpiration());

        refreshTokenRepository.save(RefreshToken.create(hash(refreshToken), user, refreshExpiresAt));

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getAccessExpiration() / 1000,
                jwtProperties.refreshExpiration() / 1000,
                userMapper.toResponse(user)
        );
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 não disponível.", exception);
        }
    }
}
