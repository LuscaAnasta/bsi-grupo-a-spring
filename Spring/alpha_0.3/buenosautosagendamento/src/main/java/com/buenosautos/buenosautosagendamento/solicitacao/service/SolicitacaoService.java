package com.buenosautos.buenosautosagendamento.solicitacao.service;

import com.buenosautos.buenosautosagendamento.solicitacao.model.Solicitacao;
import com.buenosautos.buenosautosagendamento.solicitacao.repository.SolicitacaoRepository;
import com.buenosautos.buenosautosagendamento.solicitacao.event.SolicitacaoEvent;
import com.buenosautos.buenosautosagendamento.solicitacao.model.ServicoLocal;
import com.buenosautos.buenosautosagendamento.solicitacao.model.Cliente;
import com.buenosautos.buenosautosagendamento.solicitacao.model.Veiculo;
import com.buenosautos.buenosautosagendamento.disponibilidade.model.Disponibilidade; // Import de Disponibilidade
import com.buenosautos.buenosautosagendamento.disponibilidade.service.DisponibilidadeService; // Import de DisponibilidadeService
import com.buenosautos.buenosautosagendamento.solicitacao.dto.SolicitacaoFormDTO; // Import de SolicitacaoFormDTO
import com.buenosautos.buenosautosagendamento.solicitacao.repository.ClienteRepository;
import com.buenosautos.buenosautosagendamento.solicitacao.repository.VeiculoRepository;
import com.buenosautos.buenosautosagendamento.solicitacao.repository.ServicoLocalRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime; // Adicionado para a combinação de data e hora
import java.time.format.DateTimeFormatter; // Adicionado para formatação de mensagens de erro
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SolicitacaoService {

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    @Qualifier("solicitacaoServicoLocalRepository")
    private ServicoLocalRepository solicitacaoServicoLocalRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Autowired
    private DisponibilidadeService disponibilidadeService;


    @Transactional
    public Solicitacao criarSolicitacao(SolicitacaoFormDTO solicitacaoFormDTO) {
        // --- Lógica de verificação de disponibilidade ---
        // AGORA ACESSA OS NOVOS CAMPOS SEPARADOS DO DTO
        LocalDate dataAgendadaDia = solicitacaoFormDTO.getDataAgendadaDia();
        LocalTime horarioAgendado = solicitacaoFormDTO.getHorarioAgendado();

        // Combina a data e a hora do DTO em um LocalDateTime para a entidade Solicitacao
        LocalDateTime dataHoraAgendadaCompleta = dataAgendadaDia.atTime(horarioAgendado);
        
        // 1. Consulta a disponibilidade para o dia desejado
        Optional<Disponibilidade> optDisponibilidade = disponibilidadeService.consultarDisponibilidadeParaAgendamento(dataAgendadaDia);

        // 2. Verifica se o dia está realmente disponível para agendamento
        if (optDisponibilidade.isEmpty()) {
            throw new IllegalArgumentException("O dia " + dataAgendadaDia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " não está disponível para agendamento. Pode estar fechado, lotado, ou já passou.");
        }
        
        Disponibilidade disponibilidadeDoDia = optDisponibilidade.get();

        // 3. Verifica se o horário específico está dentro do intervalo de trabalho definido para o dia.
        if (horarioAgendado.isBefore(disponibilidadeDoDia.getHorarioInicio()) || horarioAgendado.isAfter(disponibilidadeDoDia.getHorarioFim())) {
            throw new IllegalArgumentException("O horário " + horarioAgendado.format(DateTimeFormatter.ofPattern("HH:mm")) + 
                                               " está fora do horário de trabalho configurado para o dia " + dataAgendadaDia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                                               " (" + disponibilidadeDoDia.getHorarioInicio().format(DateTimeFormatter.ofPattern("HH:mm")) + 
                                               " - " + disponibilidadeDoDia.getHorarioFim().format(DateTimeFormatter.ofPattern("HH:mm")) + ").");
        }


        // --- Lógica existente para criar e salvar Cliente, Veiculo e Solicitacao ---
        Cliente cliente = new Cliente(
            solicitacaoFormDTO.getClienteNome(),
            solicitacaoFormDTO.getClienteCpf(),
            solicitacaoFormDTO.getClienteEmail(),
            solicitacaoFormDTO.getClienteTelefone()
        );
        Cliente clienteSalvo = clienteRepository.save(cliente);

        Veiculo veiculo = new Veiculo(
            solicitacaoFormDTO.getVeiculoMarca(),
            solicitacaoFormDTO.getVeiculoModelo(),
            solicitacaoFormDTO.getVeiculoAno(),
            solicitacaoFormDTO.getVeiculoPlaca()
        );
        Veiculo veiculoSalvo = veiculoRepository.save(veiculo);

        List<ServicoLocal> servicosSelecionados = solicitacaoFormDTO.getServicoIds().stream()
            .map(id -> solicitacaoServicoLocalRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Serviço selecionado com ID " + id + " não encontrado na cópia local.")
            ))
            .collect(Collectors.toList());

        Solicitacao solicitacao = new Solicitacao(
            clienteSalvo,
            servicosSelecionados,
            veiculoSalvo,
            dataHoraAgendadaCompleta // AGORA USA O LocalDateTime COMBINADO
        );
        solicitacao.setDataSolicitacao(LocalDate.now());

        Solicitacao salva = solicitacaoRepository.save(solicitacao);

        // --- Reserva a vaga no DisponibilidadeService após a criação da solicitação ---
        try {
            disponibilidadeService.reservarVagaNoDia(dataAgendadaDia); // Passa apenas a data do dia
            System.out.println("Vaga reservada no sistema de disponibilidade para o dia: " + dataAgendadaDia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } catch (IllegalStateException e) {
            throw new RuntimeException("Não foi possível reservar a vaga para o dia " + dataAgendadaDia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao reservar vaga de disponibilidade: " + e.getMessage(), e);
        }

        // --- Lógica existente para publicar evento ---
        SolicitacaoEvent event = new SolicitacaoEvent(
            salva.getId(),
            salva.getCliente().getNome(),
            salva.getCliente().getCpf(),
            salva.getCliente().getEmail(),
            salva.getCliente().getTelefone(),
            salva.getVeiculo().getMarca(),
            salva.getVeiculo().getModelo(),
            salva.getVeiculo().getAno(),
            salva.getVeiculo().getPlaca(),
            servicosSelecionados.stream().map(ServicoLocal::getId).collect(Collectors.toList()),
            salva.getDataSolicitacao(),
            salva.getDataAgendada(), // Continua sendo LocalDateTime
            "CREATED"
        );
        eventPublisher.publishEvent(event);
        System.out.println(">>> SolicitacaoService: Evento 'CREATED' publicado para Solicitacao ID: " + salva.getId());

        return salva;
    }

    // --- Método existente: Listar Solicitações ordenadas ---
    public List<Solicitacao> listarSolicitacoesMaisAtuais() {
        return solicitacaoRepository.findAllByOrderByDataAgendadaDesc();
    }
}