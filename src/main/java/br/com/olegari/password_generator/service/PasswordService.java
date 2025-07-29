package br.com.olegari.password_generator.service;

import br.com.olegari.password_generator.domain.PasswordToken;
import br.com.olegari.password_generator.repository.PasswordTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordTokenRepository passwordTokenRepository;
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int PASSWORD_LENGTH = 20;
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC_CHARS = "0123456789";

    private static final String ALL_ALLOWED_CHARS = UPPERCASE_CHARS + LOWERCASE_CHARS + NUMERIC_CHARS;

    private static final ZoneId BRASIL_ZONE_ID = ZoneId.of("America/Sao_Paulo");


    @Transactional
    public synchronized PasswordToken getCurrentActiveToken() {
        return passwordTokenRepository.findFirstByExpiresAtAfterOrderByCreatedAtDesc(Instant.now())
                .orElseGet(this::createNewToken);
    }

    public boolean isTokenValid(String tokenToValidate) {
        if (tokenToValidate == null || tokenToValidate.isBlank()) {
            return false;
        }
        Optional<PasswordToken> activeTokenOpt = passwordTokenRepository.findFirstByExpiresAtAfterOrderByCreatedAtDesc(Instant.now());
        return activeTokenOpt.isPresent() && activeTokenOpt.get().getTokenValue().equals(tokenToValidate);
    }

    private PasswordToken createNewToken() {
        String generatedPassword;
        do {
            generatedPassword = generateRandomPassword();
        } while (passwordTokenRepository.existsByTokenValue(generatedPassword));

        PasswordToken newToken = new PasswordToken();
        newToken.setTokenValue(generatedPassword);


        ZonedDateTime nowInBrazil = ZonedDateTime.now(BRASIL_ZONE_ID);
        ZonedDateTime expirationInBrazil = nowInBrazil.truncatedTo(ChronoUnit.HOURS).plusHours(1);
        Instant expirationInstant = expirationInBrazil.toInstant();

        newToken.setExpiresAt(expirationInstant);

        return passwordTokenRepository.save(newToken);
    }

    private String generateRandomPassword() {
        Stream<Character> requiredChars = Stream.of(
                UPPERCASE_CHARS.charAt(RANDOM.nextInt(UPPERCASE_CHARS.length())),
                LOWERCASE_CHARS.charAt(RANDOM.nextInt(LOWERCASE_CHARS.length())),
                NUMERIC_CHARS.charAt(RANDOM.nextInt(NUMERIC_CHARS.length()))
        );

        Stream<Character> randomChars = RANDOM.ints(PASSWORD_LENGTH - 3, 0, ALL_ALLOWED_CHARS.length())
                .mapToObj(ALL_ALLOWED_CHARS::charAt);

        List<Character> allCharsList = Stream.concat(requiredChars, randomChars).collect(Collectors.toList());

        Collections.shuffle(allCharsList, RANDOM);

        return allCharsList.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}