package br.com.olegari.password_generator.service;

import br.com.olegari.password_generator.domain.PasswordToken;
import br.com.olegari.password_generator.repository.PasswordTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordTokenRepository passwordTokenRepository;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int PASSWORD_LENGTH = 20;

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+<>?";
    private static final String ALL_CHARS = UPPER + LOWER + DIGITS + SPECIAL;

    @Transactional
    public PasswordToken generateUniquePasswordToken() {
        String generatedPassword;

        // Loop para garantir que a senha gerada seja única
        do {
            generatedPassword = generateRandomPassword();
        } while (passwordTokenRepository.existsByTokenValue(generatedPassword));

        PasswordToken newToken = new PasswordToken();
        newToken.setTokenValue(generatedPassword);
        newToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        return passwordTokenRepository.save(newToken);
    }

    private String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        // 1. Garante pelo menos um caractere de cada tipo
        password.append(UPPER.charAt(RANDOM.nextInt(UPPER.length())));
        password.append(LOWER.charAt(RANDOM.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length())));

        // 2. Preenche o restante do comprimento da senha com caracteres aleatórios de todos os conjuntos
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password.append(ALL_CHARS.charAt(RANDOM.nextInt(ALL_CHARS.length())));
        }
        
        // 3. Embaralha a senha para que os caracteres garantidos não fiquem sempre no início
        List<Character> chars = password.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        Collections.shuffle(chars, RANDOM);

        return chars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}