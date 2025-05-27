package com.buenosautos.buenosautosagendamento.solicitacao.repository;

import com.buenosautos.buenosautosagendamento.solicitacao.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long>{

}
