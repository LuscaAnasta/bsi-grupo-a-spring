package com.buenosautos.buenosautosagendamento.solicitacao.repository;

import com.buenosautos.buenosautosagendamento.solicitacao.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>{

}
