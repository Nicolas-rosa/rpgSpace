package com.rpgspace.modules.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Nome de usuário é obrigatório.")
        @Size(min = 3, max = 30, message = "Nome de usuário deve ter entre 3 e 30 caracteres.")
        @Pattern(regexp = "^[\\p{L}\\p{N}_-]+$", message = "Nome de usuário contém caracteres inválidos.")
        String username,

        @NotBlank(message = "E-mail é obrigatório.")
        @Email(message = "E-mail inválido.")
        @Size(max = 255, message = "E-mail deve ter no máximo 255 caracteres.")
        String email,

        @NotBlank(message = "Senha é obrigatória.")
        @Size(min = 8, max = 72, message = "Senha deve ter entre 8 e 72 caracteres.")
        String password
) {
}
