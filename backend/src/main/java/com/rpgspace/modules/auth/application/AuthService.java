package com.rpgspace.modules.auth.application;

import com.rpgspace.modules.auth.api.dto.AuthResponse;
import com.rpgspace.modules.auth.api.dto.LoginRequest;
import com.rpgspace.modules.auth.api.dto.RegisterRequest;
import com.rpgspace.modules.auth.api.dto.UserResponse;
import com.rpgspace.modules.auth.api.mapper.UserMapper;
import com.rpgspace.modules.user.application.UserService;
import com.rpgspace.modules.user.domain.User;
import com.rpgspace.shared.exception.UnauthorizedException;
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

    @Transactional(readOnly = true)
    public UserResponse me(String email) {
        return userMapper.toResponse(userService.requireEnabledByEmail(email));
    }

    private AuthResponse authenticate(User user) {
        return new AuthResponse(jwtService.generate(user), "Bearer", jwtServiceExpirationInSeconds(), userMapper.toResponse(user));
    }

    private long jwtServiceExpirationInSeconds() {
        return jwtServiceExpirationInMilliseconds() / 1000;
    }

    private long jwtServiceExpirationInMilliseconds() {
        return jwtService.getExpiration();
    }
}
