package com.buenosautos.buenosautosagendamento.servico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.buenosautos.buenosautosagendamento.solicitacao.model.ServicoCliente;

@Repository
public interface ServicoRepository extends JpaRepository<ServicoCliente, Long> {

	
}