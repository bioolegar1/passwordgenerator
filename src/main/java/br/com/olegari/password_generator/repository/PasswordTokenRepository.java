package br.com.olegari.password_generator.repository;

import br.com.olegari.password_generator.domain.PasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Long> {

    boolean existsByTokenValue(String tokenValue);

    Optional<PasswordToken> findFirstByExpiresAtAfterOrderByCreatedAtDesc(Instant now);
}