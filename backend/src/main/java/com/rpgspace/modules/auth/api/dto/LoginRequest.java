package com.rpgspace.modules.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "E-mail é obrigatório.")
        @Email(message = "E-mail inválido.")
        @Size(max = 255, message = "E-mail deve ter no máximo 255 caracteres.")
        String email,

        @NotBlank(message = "Senha é obrigatória.")
        @Size(max = 72, message = "Senha deve ter no máximo 72 caracteres.")
        String password
) {
}
