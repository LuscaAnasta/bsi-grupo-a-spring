package com.buenosautos.buenosautosagendamento.login.repository;

import com.buenosautos.buenosautosagendamento.login.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    // Busca um token pelo seu valor em String
    Optional<PasswordResetToken> findByToken(String token);
    // Opcional: Para limpar tokens antigos ou múltiplos de um mesmo usuário
    void deleteByUserId(Long userId);
}