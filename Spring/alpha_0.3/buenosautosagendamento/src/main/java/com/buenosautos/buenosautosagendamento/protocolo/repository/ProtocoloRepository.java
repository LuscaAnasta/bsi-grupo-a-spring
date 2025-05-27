package com.buenosautos.buenosautosagendamento.protocolo.repository;

import com.buenosautos.buenosautosagendamento.protocolo.model.Protocolo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProtocoloRepository extends JpaRepository<Protocolo, Long> {
    Optional<Protocolo> findBySolicitacao_Id(Long solicitacaoId);
    List<Protocolo> findAllByOrderByDataProtocoloDesc();
    Optional<Protocolo> findByNumeroProtocolo(String numeroProtocolo);
}