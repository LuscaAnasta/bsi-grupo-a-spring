package com.buenosautos.buenosautosagendamento.solicitacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.buenosautos.buenosautosagendamento.solicitacao.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Adicione customizações se necessário
}