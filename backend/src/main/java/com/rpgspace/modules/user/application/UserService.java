package com.rpgspace.modules.user.application;

import com.rpgspace.modules.user.domain.User;
import com.rpgspace.modules.user.infrastructure.persistence.UserRepository;
import com.rpgspace.shared.exception.ConflictException;
import com.rpgspace.shared.exception.UnauthorizedException;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User create(String username, String email, String passwordHash) {
        String normalizedUsername = username.trim();
        String normalizedEmail = normalizeEmail(email);

        if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            throw new ConflictException("Nome de usuário já está em uso.");
        }
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException("E-mail já está em uso.");
        }
        return userRepository.save(User.create(normalizedUsername, normalizedEmail, passwordHash));
    }

    @Transactional(readOnly = true)
    public User requireEnabledByEmail(String email) {
        return findEnabledByEmail(email).orElseThrow(() -> new UnauthorizedException("Credenciais inválidas."));
    }

    @Transactional(readOnly = true)
    public Optional<User> findEnabledByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(normalizeEmail(email)).filter(User::isEnabled);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
