package com.buenosautos.buenosautosagendamento.solicitacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.buenosautos.buenosautosagendamento.solicitacao.model.Veiculo;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    // Adicione customizações se necessário
}