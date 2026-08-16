package com.rpgspace.modules.auth.api.dto;

import com.rpgspace.modules.user.domain.Role;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(UUID id, String username, String email, Role role, Instant createdAt) {
}
