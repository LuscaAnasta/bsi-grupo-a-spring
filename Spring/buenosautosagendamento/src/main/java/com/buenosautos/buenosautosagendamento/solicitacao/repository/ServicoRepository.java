package com.buenosautos.buenosautosagendamento.solicitacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.buenosautos.buenosautosagendamento.solicitacao.model.Servico;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

	
}