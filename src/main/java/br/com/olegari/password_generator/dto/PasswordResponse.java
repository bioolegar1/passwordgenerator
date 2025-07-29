package br.com.olegari.password_generator.dto;

import java.time.Instant; // MUDANÇA IMPORTANTE

// O campo "expiresAt" agora é um Instant
// A formatação @JsonFormat foi removida para usar o padrão global (UTC)
public record PasswordResponse(
        String token,
        Instant expiresAt
) {}