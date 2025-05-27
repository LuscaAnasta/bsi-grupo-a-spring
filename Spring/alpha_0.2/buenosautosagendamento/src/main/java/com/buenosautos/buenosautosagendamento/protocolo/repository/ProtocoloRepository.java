package com.buenosautos.buenosautosagendamento.protocolo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buenosautos.buenosautosagendamento.protocolo.model.Protocolo;

@Repository
public interface ProtocoloRepository extends JpaRepository<Protocolo, Long> { 
	
}
