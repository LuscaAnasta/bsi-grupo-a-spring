package com.buenosautos.buenosautosagendamento.solicitacao.service;

import com.buenosautos.buenosautosagendamento.solicitacao.model.Solicitacao;
import com.buenosautos.buenosautosagendamento.solicitacao.repository.SolicitacaoRepository;
import com.buenosautos.buenosautosagendamento.solicitacao.event.SolicitacaoEvent;
import com.buenosautos.buenosautosagendamento.solicitacao.model.ServicoLocal;
import com.buenosautos.buenosautosagendamento.solicitacao.model.Cliente;
import com.buenosautos.buenosautosagendamento.solicitacao.model.Veiculo;
import com.buenosautos.buenosautosagendamento.disponibilidade.model.Disponibilidade;
import com.buenosautos.buenosautosagendamento.disponibilidade.service.DisponibilidadeService;
import com.buenosautos.buenosautosagendamento.solicitacao.dto.SolicitacaoFormDTO;
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
    
    @Autowired // NOVO: Injeção do DisponibilidadeService
    private DisponibilidadeService disponibilidadeService;


@Transactional
public Solicitacao criarSolicitacao(SolicitacaoFormDTO solicitacaoFormDTO) {
    // --- NOVO: Lógica de verificação de disponibilidade ---
    LocalDate dataAgendadaDia = solicitacaoFormDTO.getDataAgendada().toLocalDate();
    
    // 1. Consulta a disponibilidade para o dia desejado
    Optional<Disponibilidade> optDisponibilidade = disponibilidadeService.consultarDisponibilidadeParaAgendamento(dataAgendadaDia);

    // 2. Verifica se o dia está realmente disponível para agendamento
    if (optDisponibilidade.isEmpty()) {
        throw new IllegalArgumentException("O dia " + dataAgendadaDia.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " não está disponível para agendamento. Pode estar fechado, lotado, ou já passou.");
    }
    
    Disponibilidade disponibilidadeDoDia = optDisponibilidade.get();

    // Opcional: Verifique se o horário específico está dentro do intervalo de trabalho definido para o dia.
    // Embora a capacidade seja diária, o agendamento precisa estar dentro do horário de trabalho.
    LocalTime horarioAgendado = solicitacaoFormDTO.getDataAgendada().toLocalTime();
    if (horarioAgendado.isBefore(disponibilidadeDoDia.getHorarioInicio()) || horarioAgendado.isAfter(disponibilidadeDoDia.getHorarioFim())) {
        throw new IllegalArgumentException("O horário " + horarioAgendado.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) + 
                                           " está fora do horário de trabalho configurado para o dia " + dataAgendadaDia.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                                           " (" + disponibilidadeDoDia.getHorarioInicio().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) + 
                                           " - " + disponibilidadeDoDia.getHorarioFim().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) + ").");
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
        solicitacaoFormDTO.getVeiculoPlaca() // Agora com a placa
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
        solicitacaoFormDTO.getDataAgendada()
    );
    solicitacao.setDataSolicitacao(LocalDate.now());

    Solicitacao salva = solicitacaoRepository.save(solicitacao);

    // --- NOVO: Reserva a vaga no DisponibilidadeService após a criação da solicitação ---
    try {
        disponibilidadeService.reservarVagaNoDia(dataAgendadaDia);
        System.out.println("Vaga reservada no sistema de disponibilidade para o dia: " + dataAgendadaDia);
    } catch (IllegalStateException e) {
        // Isso deve ser um erro que o service de disponibilidade já tratou (capacidade atingida, etc.)
        // Rollback da transação do Spring se a reserva falhar (devido a @Transactional)
        throw new RuntimeException("Não foi possível reservar a vaga para o dia " + dataAgendadaDia + ": " + e.getMessage(), e);
    } catch (Exception e) {
        throw new RuntimeException("Erro inesperado ao reservar vaga de disponibilidade: " + e.getMessage(), e);
    }
    // --- FIM DA NOVA LÓGICA DE DISPONIBILIDADE ---


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
        salva.getDataAgendada(),
        "CREATED"
    );
    eventPublisher.publishEvent(event);
    System.out.println(">>> SolicitacaoService: Evento 'CREATED' publicado para Solicitacao ID: " + salva.getId());

    return salva;
}

    // --- NOVO MÉTODO: Listar Solicitações ordenadas ---
    public List<Solicitacao> listarSolicitacoesMaisAtuais() {
        // Ordena por dataSolicitacao (data de criação) em ordem decrescente
        // ou por dataAgendada (data agendada) em ordem decrescente, dependendo do que você considera "mais atual".
        // Vamos usar dataSolicitacao para "mais recente criada" e dataAgendada para "próximos agendamentos".
        // Para "mais atuais" no sentido de "recentemente criadas":
        // return solicitacaoRepository.findAllByOrderByDataSolicitacaoDesc();

        // Para "mais atuais" no sentido de "próximos agendamentos":
        // Você pode filtrar por dataAgendada no futuro ou presente também
        return solicitacaoRepository.findAllByOrderByDataAgendadaDesc();
    }
}