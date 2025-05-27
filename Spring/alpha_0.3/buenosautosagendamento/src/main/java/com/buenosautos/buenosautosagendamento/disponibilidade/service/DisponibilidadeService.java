package com.buenosautos.buenosautosagendamento.disponibilidade.service;

import com.buenosautos.buenosautosagendamento.disponibilidade.model.Disponibilidade;
import com.buenosautos.buenosautosagendamento.disponibilidade.repository.DisponibilidadeRepository;
import com.buenosautos.buenosautosagendamento.disponibilidade.dto.DiaDisponivelDTO; // Adicionado para o DTO de retorno
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DisponibilidadeService {

    private final DisponibilidadeRepository disponibilidadeRepository;

    // Valores padrão para dias não configurados
    private final LocalTime HORARIO_INICIO_PADRAO = LocalTime.of(9, 0); // 09:00
    private final LocalTime HORARIO_FIM_PADRAO = LocalTime.of(17, 0);   // 17:00
    private final int CAPACIDADE_PADRAO = 4;                            // 4 vagas

    @Autowired
    public DisponibilidadeService(DisponibilidadeRepository disponibilidadeRepository) {
        this.disponibilidadeRepository = disponibilidadeRepository;
    }

    // --- Métodos de Gestão da Disponibilidade (para o usuário/admin) ---

    @Transactional
    public Disponibilidade definirDisponibilidadeDiaria(LocalDate dia, LocalTime horarioInicio, LocalTime horarioFim, int capacidadeMaxima) {
        validarPeriodoDefinicao(dia);

        if (horarioInicio.isAfter(horarioFim) || horarioInicio.equals(horarioFim)) {
            throw new IllegalArgumentException("O horário de início (" + horarioInicio + ") deve ser anterior ao horário de fim (" + horarioFim + ").");
        }
        if (ChronoUnit.HOURS.between(horarioInicio, horarioFim) < 1) {
            throw new IllegalArgumentException("O intervalo de trabalho deve ser de no mínimo 1 hora.");
        }

        Optional<Disponibilidade> optDisponibilidade = disponibilidadeRepository.findByDia(dia);
        Disponibilidade disponibilidade;

        if (optDisponibilidade.isPresent()) {
            disponibilidade = optDisponibilidade.get();
            // Referência CORRETA para capacidadeMaximaDia
            if (capacidadeMaxima < disponibilidade.getAgendamentosNoDia()) { 
                throw new IllegalArgumentException("Nova capacidade máxima (" + capacidadeMaxima + ") é menor que os agendamentos já existentes (" + disponibilidade.getAgendamentosNoDia() + ") para o dia " + dia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".");
            }
            System.out.println("Atualizando disponibilidade existente para o dia: " + dia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            disponibilidade = new Disponibilidade();
            disponibilidade.setDia(dia);
            System.out.println("Criando nova disponibilidade para o dia: " + dia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        disponibilidade.setHorarioInicio(horarioInicio);
        disponibilidade.setHorarioFim(horarioFim);
        // Referência CORRETA para capacidadeMaximaDia
        disponibilidade.setCapacidadeMaximaDia(capacidadeMaxima);
        // Referência CORRETA para StatusDisponibilidadeDiaria
        disponibilidade.setStatusDia(Disponibilidade.StatusDisponibilidadeDiaria.ABERTO); 

        return disponibilidadeRepository.save(disponibilidade);
    }

    @Transactional
    // Referência CORRETA para StatusDisponibilidadeDiaria
    public Disponibilidade alterarStatusDisponibilidade(LocalDate dia, Disponibilidade.StatusDisponibilidadeDiaria novoStatus) {
        Disponibilidade disponibilidade = disponibilidadeRepository.findByDia(dia)
            .orElseThrow(() -> new IllegalArgumentException("Disponibilidade para o dia " + dia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " não encontrada para alteração de status."));

        // Referência CORRETA para statusDia
        disponibilidade.setStatusDia(novoStatus);
        return disponibilidadeRepository.save(disponibilidade);
    }

    // --- Métodos de Consulta de Disponibilidade (para o cliente/solicitação) ---

    public Optional<Disponibilidade> consultarDisponibilidadeParaAgendamento(LocalDate dataDesejada) {
        validarPeriodoDefinicao(dataDesejada); 

        Optional<Disponibilidade> optDisponibilidade = disponibilidadeRepository.findByDia(dataDesejada);
        Disponibilidade disponibilidadeParaAvaliacao;

        if (optDisponibilidade.isPresent()) {
            disponibilidadeParaAvaliacao = optDisponibilidade.get();
        } else {
            disponibilidadeParaAvaliacao = new Disponibilidade(dataDesejada, HORARIO_INICIO_PADRAO, HORARIO_FIM_PADRAO, CAPACIDADE_PADRAO);
            System.out.println("Usando disponibilidade padrão para avaliação do dia: " + dataDesejada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        if (!disponibilidadeParaAvaliacao.estaAbertoParaNovosAgendamentos()) {
            // Referência CORRETA para statusDia e capacidadeMaximaDia
            System.out.println("Dia " + dataDesejada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " (configurado ou padrão) não está aberto ou está cheio. Status: " + disponibilidadeParaAvaliacao.getStatusDia() + ", Agendamentos: " + disponibilidadeParaAvaliacao.getAgendamentosNoDia() + "/" + disponibilidadeParaAvaliacao.getCapacidadeMaximaDia());
            return Optional.empty();
        }

        if (disponibilidadeParaAvaliacao.jaPassouHorarioFinal()) {
            System.out.println("Dia " + dataDesejada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " (configurado ou padrão) já passou o horário de fim (" + disponibilidadeParaAvaliacao.getHorarioFim() + ").");
            return Optional.empty();
        }
        
        return Optional.of(disponibilidadeParaAvaliacao);
    }

    public List<DiaDisponivelDTO> listarDiasComHorariosDisponiveisParaAgendamento() {
        LocalDate hoje = LocalDate.now();
        LocalDate quinzeDiasAFrente = hoje.plusDays(14);

        List<Disponibilidade> disponibilidadesConfiguradasNoBanco = 
            disponibilidadeRepository.findByDiaBetween(hoje, quinzeDiasAFrente);
        
        Map<LocalDate, Disponibilidade> mapaDisponibilidades = disponibilidadesConfiguradasNoBanco.stream()
            .collect(Collectors.toMap(Disponibilidade::getDia, d -> d));

        List<DiaDisponivelDTO> diasComHorariosDisponiveis = new ArrayList<>();

        for (long i = 0; i <= 14; i++) {
            LocalDate diaAtual = hoje.plusDays(i);
            Disponibilidade dispoParaAvaliacao;

            if (mapaDisponibilidades.containsKey(diaAtual)) {
                dispoParaAvaliacao = mapaDisponibilidades.get(diaAtual);
            } else {
                dispoParaAvaliacao = new Disponibilidade(diaAtual, HORARIO_INICIO_PADRAO, HORARIO_FIM_PADRAO, CAPACIDADE_PADRAO);
            }

            if (dispoParaAvaliacao.estaAbertoParaNovosAgendamentos() && !dispoParaAvaliacao.jaPassouHorarioFinal()) {
                List<LocalTime> horariosFixosDoDia = new ArrayList<>();
                LocalTime horarioGerado = dispoParaAvaliacao.getHorarioInicio();
                
                while (horarioGerado.isBefore(dispoParaAvaliacao.getHorarioFim())) {
                    LocalDateTime momentoFimDoSlot = diaAtual.atTime(horarioGerado.plusHours(1));
                    if (momentoFimDoSlot.isAfter(LocalDateTime.now())) {
                         horariosFixosDoDia.add(horarioGerado);
                    }
                    horarioGerado = horarioGerado.plusHours(1);
                }

                if (!horariosFixosDoDia.isEmpty()) {
                    diasComHorariosDisponiveis.add(new DiaDisponivelDTO(diaAtual, horariosFixosDoDia));
                }
            }
        }
        return diasComHorariosDisponiveis;
    }

    // --- Métodos para Ocupar/Liberar Vagas (chamados por outros serviços/listeners) ---

    @Transactional
    public void reservarVagaNoDia(LocalDate dia) {
        Disponibilidade disponibilidade = consultarDisponibilidadeParaAgendamento(dia)
            .orElseThrow(() -> new IllegalArgumentException("Não foi possível reservar vaga para o dia " + dia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ": o dia não está disponível ou já passou."));

        if (!disponibilidade.estaAbertoParaNovosAgendamentos()) {
            throw new IllegalStateException("O dia " + dia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " não está aberto ou já atingiu a capacidade máxima para agendamento.");
        }
        
        if (disponibilidade.getId() != null) {
             disponibilidade.incrementarAgendamentoNoDia();
             disponibilidadeRepository.save(disponibilidade);
        } else {
             disponibilidade.incrementarAgendamentoNoDia();
             disponibilidadeRepository.save(disponibilidade);
        }
       
        // Referência CORRETA para capacidadeMaximaDia
        System.out.println("Vaga reservada para o dia " + dia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ". Agendamentos no dia: " + disponibilidade.getAgendamentosNoDia() + "/" + disponibilidade.getCapacidadeMaximaDia());
    }

    @Transactional
    public void liberarVagaNoDia(LocalDate dia) {
        Disponibilidade disponibilidade = disponibilidadeRepository.findByDia(dia)
            .orElseThrow(() -> new IllegalArgumentException("Disponibilidade para o dia " + dia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " não encontrada para liberação."));
        
        disponibilidade.decrementarAgendamentoNoDia(); 
        disponibilidadeRepository.save(disponibilidade);
        // Referência CORRETA para capacidadeMaximaDia
        System.out.println("Vaga liberada para o dia " + dia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ". Agendamentos no dia: " + disponibilidade.getAgendamentosNoDia() + "/" + disponibilidade.getCapacidadeMaximaDia());
    }

    // --- Métodos Auxiliares de Validação ---

    private void validarPeriodoDefinicao(LocalDate dia) {
        LocalDate hoje = LocalDate.now();
        LocalDate limiteSuperior = hoje.plusDays(14);

        if (dia.isBefore(hoje)) {
            throw new IllegalArgumentException("Não é possível definir disponibilidade para dias passados. Data informada: " + dia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".");
        }
        if (dia.isAfter(limiteSuperior)) {
            throw new IllegalArgumentException("Não é possível definir disponibilidade para mais de 15 dias à frente. Data informada: " + dia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ", limite: " + limiteSuperior.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".");
        }
    }
}