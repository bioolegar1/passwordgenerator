package br.com.olegari.password_generator.repository; // Verifique esta linha

import br.com.olegari.password_generator.domain.PasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Long> { // Verifique esta linha

    /**
     * Verifica de forma otimizada se um token com o valor fornecido já existe no banco.
     * @param tokenValue O valor do token a ser verificado.
     * @return true se o token existir, false caso contrário.
     */
    boolean existsByTokenValue(String tokenValue);
}