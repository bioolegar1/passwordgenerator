package br.com.olegari.password_generator.dto;

import java.time.Instant;


public record PasswordResponse(
        String token,
        Instant expiresAt
) {}