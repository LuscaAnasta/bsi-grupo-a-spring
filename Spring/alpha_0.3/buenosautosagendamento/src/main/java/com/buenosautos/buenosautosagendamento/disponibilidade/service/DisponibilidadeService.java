package com.buenosautos.buenosautosagendamento.disponibilidade.service;

import com.buenosautos.buenosautosagendamento.disponibilidade.model.Disponibilidade;
import com.buenosautos.buenosautosagendamento.disponibilidade.repository.DisponibilidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit; // Para calcular diferença em dias
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Indica que é um componente de serviço do Spring
public class DisponibilidadeService {

    private final DisponibilidadeRepository disponibilidadeRepository;

    // Usamos injeção por construtor, que é uma boa prática
    @Autowired
    public DisponibilidadeService(DisponibilidadeRepository disponibilidadeRepository) {
        this.disponibilidadeRepository = disponibilidadeRepository;
    }

    // --- Métodos de Gestão da Disponibilidade (para o usuário/admin) ---

    /**
     * Define ou atualiza a disponibilidade para um dia específico.
     * Inclui validação para o limite de 15 dias à frente e horários.
     * @param dia O dia para o qual a disponibilidade será definida.
     * @param horarioInicio O horário de início do período de trabalho para o dia.
     * @param horarioFim O horário de fim do período de trabalho para o dia.
     * @param capacidadeMaxima A capacidade total de agendamentos para o dia.
     * @return A entidade Disponibilidade salva/atualizada.
     * @throws IllegalArgumentException se as regras de negócio forem violadas.
     */
    @Transactional
    public Disponibilidade definirDisponibilidadeDiaria(LocalDate dia, LocalTime horarioInicio, LocalTime horarioFim, int capacidadeMaxima) {
        // 1. Validação de Regras de Negócio (15 dias à frente)
        validarPeriodoDefinicao(dia);

        // 2. Validação de Horários
        if (horarioInicio.isAfter(horarioFim) || horarioInicio.equals(horarioFim)) {
            throw new IllegalArgumentException("O horário de início deve ser anterior ao horário de fim.");
        }
        // Opcional: Validar se o gap mínimo de 1 hora entre inicio e fim está sendo respeitado
        // if (ChronoUnit.HOURS.between(horarioInicio, horarioFim) < 1) {
        //     throw new IllegalArgumentException("O intervalo de trabalho deve ser de no mínimo 1 hora.");
        // }


        // 3. Busca ou Cria a entidade Disponibilidade para o dia
        Optional<Disponibilidade> optDisponibilidade = disponibilidadeRepository.findByDia(dia);
        Disponibilidade disponibilidade;

        if (optDisponibilidade.isPresent()) {
            disponibilidade = optDisponibilidade.get();
            // Evitar sobrescrever contagem de agendamentos se a disponibilidade for atualizada
            if (disponibilidade.getCapacidadeMaximaDia() != capacidadeMaxima) {
                // Se a capacidade máxima mudar, talvez você precise de lógica para ajustar agendamentosNoDia
                // ou apenas permitir a mudança e validar agendamentosNoDia posteriormente.
                // Por enquanto, apenas atualiza a capacidade.
                 if (capacidadeMaxima < disponibilidade.getAgendamentosNoDia()) {
                    throw new IllegalArgumentException("Nova capacidade máxima (" + capacidadeMaxima + ") é menor que os agendamentos já existentes (" + disponibilidade.getAgendamentosNoDia() + ") para o dia " + dia);
                }
            }
             System.out.println("Atualizando disponibilidade existente para o dia: " + dia);
        } else {
            disponibilidade = new Disponibilidade();
            disponibilidade.setDia(dia);
            System.out.println("Criando nova disponibilidade para o dia: " + dia);
        }

        // 4. Atualiza os detalhes da disponibilidade
        disponibilidade.setHorarioInicio(horarioInicio);
        disponibilidade.setHorarioFim(horarioFim);
        disponibilidade.setCapacidadeMaximaDia(capacidadeMaxima);
        disponibilidade.setStatusDia(Disponibilidade.StatusDisponibilidadeDiaria.ABERTO); // Sempre ABERTO ao ser definido

        return disponibilidadeRepository.save(disponibilidade);
    }

    /**
     * Altera o status gerencial de um dia (ABERTO/FECHADO).
     * @param dia O dia a ser atualizado.
     * @param novoStatus O novo status desejado.
     * @return A entidade Disponibilidade atualizada.
     * @throws IllegalArgumentException se o dia não for encontrado.
     */
    @Transactional
    public Disponibilidade alterarStatusDisponibilidade(LocalDate dia, Disponibilidade.StatusDisponibilidadeDiaria novoStatus) {
        Disponibilidade disponibilidade = disponibilidadeRepository.findByDia(dia)
            .orElseThrow(() -> new IllegalArgumentException("Disponibilidade para o dia " + dia + " não encontrada."));

        disponibilidade.setStatusDia(novoStatus);
        return disponibilidadeRepository.save(disponibilidade);
    }

    // --- Métodos de Consulta de Disponibilidade (para o cliente/solicitação) ---

    /**
     * Consulta a disponibilidade para agendamento em um dia específico.
     * Retorna o objeto Disponibilidade se o dia estiver agendável, ou Optional.empty().
     * @param dataDesejada A data para a qual se deseja consultar a disponibilidade.
     * @return Um Optional contendo a Disponibilidade se estiver disponível para agendamento, ou vazio.
     */
    public Optional<Disponibilidade> consultarDisponibilidadeParaAgendamento(LocalDate dataDesejada) {
        Optional<Disponibilidade> optDisponibilidade = disponibilidadeRepository.findByDia(dataDesejada);

        if (optDisponibilidade.isEmpty()) {
            return Optional.empty(); // Dia não configurado
        }

        Disponibilidade disponibilidade = optDisponibilidade.get();

        // 1. O dia está gerenciável como ABERTO e não atingiu a capacidade máxima?
        if (!disponibilidade.estaAbertoParaNovosAgendamentos()) {
            System.out.println("Dia " + dataDesejada + " não está aberto ou está cheio.");
            return Optional.empty();
        }

        // 2. O dia ou o horário de término já passou?
        if (disponibilidade.jaPassou() || disponibilidade.jaPassouHorarioFinal()) {
            System.out.println("Dia " + dataDesejada + " já passou.");
            return Optional.empty();
        }
        
        return Optional.of(disponibilidade);
    }

    /**
     * Retorna uma lista de dias disponíveis para agendamento nos próximos 15 dias (incluindo o atual).
     * @return Lista de objetos Disponibilidade para dias agendáveis.
     */
    public List<Disponibilidade> listarDiasAgendaveis() {
        LocalDate hoje = LocalDate.now();
        LocalDate quinzeDiasAFrente = hoje.plusDays(14); // Hoje + 14 dias = 15 dias no total

        // O Spring Data JPA pode criar este método automaticamente se você tiver findByDiaBetween
        // Ou você pode buscar tudo e filtrar em memória se a lista for pequena
        return disponibilidadeRepository.findByDiaBetween(hoje, quinzeDiasAFrente).stream()
                .filter(Disponibilidade::estaAbertoParaNovosAgendamentos) // Apenas dias com Status.ABERTO e com vagas
                .filter(dispo -> !dispo.jaPassou()) // O dia ainda não passou (apenas a data)
                .filter(dispo -> !dispo.jaPassouHorarioFinal()) // O dia não passou do horário de término
                .collect(Collectors.toList());
    }

    // --- Métodos para Ocupar/Liberar Vagas (chamados por outros serviços/listeners) ---

    /**
     * Incrementa a contagem de agendamentos para um dia específico.
     * @param dia A data do agendamento.
     * @throws IllegalArgumentException se o dia não for encontrado ou não tiver mais capacidade.
     */
    @Transactional
    public void reservarVagaNoDia(LocalDate dia) {
        Disponibilidade disponibilidade = disponibilidadeRepository.findByDia(dia)
            .orElseThrow(() -> new IllegalArgumentException("Disponibilidade para o dia " + dia + " não encontrada para reserva."));

        if (!disponibilidade.estaAbertoParaNovosAgendamentos()) {
            throw new IllegalStateException("O dia " + dia + " não está aberto ou já atingiu a capacidade máxima para agendamento.");
        }
        
        disponibilidade.incrementarAgendamentoNoDia();
        disponibilidadeRepository.save(disponibilidade);
        System.out.println("Vaga reservada para o dia " + dia + ". Agendamentos no dia: " + disponibilidade.getAgendamentosNoDia());
    }

    /**
     * Decrementa a contagem de agendamentos para um dia específico.
     * @param dia A data do agendamento.
     * @throws IllegalArgumentException se o dia não for encontrado.
     */
    @Transactional
    public void liberarVagaNoDia(LocalDate dia) {
        Disponibilidade disponibilidade = disponibilidadeRepository.findByDia(dia)
            .orElseThrow(() -> new IllegalArgumentException("Disponibilidade para o dia " + dia + " não encontrada para liberação."));
        
        disponibilidade.decrementarAgendamentoNoDia(); // Assumindo que você corrigirá o método na entidade
        disponibilidadeRepository.save(disponibilidade);
        System.out.println("Vaga liberada para o dia " + dia + ". Agendamentos no dia: " + disponibilidade.getAgendamentosNoDia());
    }


    // --- Métodos Auxiliares de Validação ---

    private void validarPeriodoDefinicao(LocalDate dia) {
        LocalDate hoje = LocalDate.now();
        LocalDate limiteSuperior = hoje.plusDays(14); // 15 dias incluindo o atual (hoje + 14)

        if (dia.isBefore(hoje)) {
            throw new IllegalArgumentException("Não é possível definir disponibilidade para dias passados.");
        }
        if (dia.isAfter(limiteSuperior)) {
            throw new IllegalArgumentException("Não é possível definir disponibilidade para mais de 15 dias à frente.");
        }
    }
}