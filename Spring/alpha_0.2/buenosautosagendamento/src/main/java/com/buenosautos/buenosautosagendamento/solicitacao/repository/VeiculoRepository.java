package com.buenosautos.buenosautosagendamento.solicitacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buenosautos.buenosautosagendamento.solicitacao.model.Veiculo;
@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    // Adicione customizações se necessário
}