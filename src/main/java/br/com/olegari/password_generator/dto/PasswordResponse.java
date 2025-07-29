package br.com.olegari.password_generator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

// Usando record do Java para um DTO conciso e imutável
public record PasswordResponse(
        String token,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime expiresAt
) {}