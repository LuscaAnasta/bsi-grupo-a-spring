package com.buenosautos.buenosautosagendamento.disponibilidade.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buenosautos.buenosautosagendamento.disponibilidade.model.Disponibilidade;
@Repository

public interface DisponibilidadeRepository extends JpaRepository<Disponibilidade, Long>{
	 Optional<Disponibilidade> findByDia(LocalDate dia);

    // Adicione este método para buscar disponibilidades dentro de um intervalo de datas
    List<Disponibilidade> findByDiaBetween(LocalDate startDay, LocalDate endDay);

}
