package com.buenosautos.buenosautosagendamento.solicitacao.repository;

import com.buenosautos.buenosautosagendamento.solicitacao.model.Solicitacao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
	 List<Solicitacao> findAllByOrderByDataAgendadaDesc();
}