package com.buenosautos.buenosautosagendamento.solicitacao.repository;

import com.buenosautos.buenosautosagendamento.solicitacao.model.ServicoLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("solicitacaoServicoLocalRepository") 
public interface ServicoLocalRepository extends JpaRepository<ServicoLocal, Long> {
}