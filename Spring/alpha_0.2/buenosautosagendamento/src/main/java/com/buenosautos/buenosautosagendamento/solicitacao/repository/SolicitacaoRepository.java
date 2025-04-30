package com.buenosautos.buenosautosagendamento.solicitacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buenosautos.buenosautosagendamento.solicitacao.model.Solicitacao;
@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
    // Adicione customizações se necessário (ex: consultas personalizadas)
}