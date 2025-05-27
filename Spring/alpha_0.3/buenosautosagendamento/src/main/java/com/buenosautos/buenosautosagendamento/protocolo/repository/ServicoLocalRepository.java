package com.buenosautos.buenosautosagendamento.protocolo.repository;

import com.buenosautos.buenosautosagendamento.protocolo.model.ServicoLocal; // Use a ServicoLocal do pacote de Protocolo
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("protocoloServicoLocalRepository") 
public interface ServicoLocalRepository extends JpaRepository<ServicoLocal, Long> {
}