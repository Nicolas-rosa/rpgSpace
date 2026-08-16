package com.rpgspace.modules.auth.api.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresIn, UserResponse user) {
}
