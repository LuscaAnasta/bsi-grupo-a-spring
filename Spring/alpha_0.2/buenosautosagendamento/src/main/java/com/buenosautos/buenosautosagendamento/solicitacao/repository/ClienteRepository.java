package com.buenosautos.buenosautosagendamento.solicitacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buenosautos.buenosautosagendamento.solicitacao.model.Cliente;
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Adicione customizações se necessário
}