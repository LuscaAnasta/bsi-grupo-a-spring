package com.buenosautos.buenosautosagendamento.solicitacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.buenosautos.buenosautosagendamento.solicitacao.model.ServicoCliente;

@Repository
public interface ServicoClienteRepository extends JpaRepository<ServicoCliente, Long> {

	
}