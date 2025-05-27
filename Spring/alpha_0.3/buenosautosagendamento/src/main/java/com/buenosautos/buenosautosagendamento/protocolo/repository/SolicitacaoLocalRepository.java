package com.buenosautos.buenosautosagendamento.protocolo.repository;

import com.buenosautos.buenosautosagendamento.protocolo.model.SolicitacaoLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitacaoLocalRepository extends JpaRepository<SolicitacaoLocal, Long> {
}