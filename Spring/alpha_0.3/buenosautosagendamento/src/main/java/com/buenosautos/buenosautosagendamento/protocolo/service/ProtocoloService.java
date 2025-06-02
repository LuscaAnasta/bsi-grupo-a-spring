package com.buenosautos.buenosautosagendamento.protocolo.service;

import com.buenosautos.buenosautosagendamento.notificacao.service.NotificacaoService;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buenosautos.buenosautosagendamento.protocolo.model.Protocolo;
import com.buenosautos.buenosautosagendamento.protocolo.model.ProtocoloServicoItem;
import com.buenosautos.buenosautosagendamento.protocolo.model.ServicoLocal;
import com.buenosautos.buenosautosagendamento.protocolo.model.SolicitacaoLocal;
import com.buenosautos.buenosautosagendamento.protocolo.repository.ProtocoloRepository;
import com.buenosautos.buenosautosagendamento.protocolo.repository.ServicoLocalRepository;
import com.buenosautos.buenosautosagendamento.protocolo.repository.SolicitacaoLocalRepository;

import com.buenosautos.buenosautosagendamento.servico.model.Servico;
import com.buenosautos.buenosautosagendamento.servico.service.ServicoService;

@Service
public class ProtocoloService {

    @Autowired
    private ProtocoloRepository protocoloRepository;

    @Autowired
    private SolicitacaoLocalRepository solicitacaoLocalRepository;

    @Autowired
    @Qualifier("protocoloServicoLocalRepository")
    private ServicoLocalRepository servicoLocalRepository;

    @Autowired
    private ServicoService servicoPrincipalService;

    @Autowired
    private NotificacaoService notificacaoService;

    @Transactional
    public Protocolo processarSolicitacaoEventAndCreateProtocolo(SolicitacaoLocal solicitacaoLocal, List<Long> servicoIdsParaProtocolo) {
        SolicitacaoLocal solicitacaoLocalSalva = solicitacaoLocalRepository.save(solicitacaoLocal);
        System.out.println("SolicitacaoLocal salva/atualizada no Protocolo: " + solicitacaoLocalSalva.getId());

        Optional<Protocolo> protocoloExistente = protocoloRepository.findBySolicitacao_Id(solicitacaoLocalSalva.getId());
        if (protocoloExistente.isPresent()) {
            System.out.println("Protocolo para SolicitacaoLocal ID " + solicitacaoLocalSalva.getId() + " já existe. Pulando criação.");
            return protocoloExistente.get();
        }

        Protocolo novoProtocolo = new Protocolo();
        novoProtocolo.setSolicitacao(solicitacaoLocalSalva);

        if (solicitacaoLocalSalva.getDataAgendada() != null) {
            novoProtocolo.setDataAgendadaProtocolo(solicitacaoLocalSalva.getDataAgendada().toLocalDate());
        } else {
            System.out.println("Aviso: Data agendada na SolicitacaoLocal é nula para ID: " + solicitacaoLocalSalva.getId() + ". Data agendada do protocolo não será definida.");
        }

        if (servicoIdsParaProtocolo != null && !servicoIdsParaProtocolo.isEmpty()) {
            List<ServicoLocal> servicosIniciaisDaSolicitacao = servicoLocalRepository.findAllById(servicoIdsParaProtocolo);

            for (ServicoLocal servicoLocal : servicosIniciaisDaSolicitacao) {
                novoProtocolo.adicionarServico(
                        servicoLocal.getId(),
                        servicoLocal.getCodigo(),
                        servicoLocal.getNome(),
                        servicoLocal.getPreco(),
                        "Serviço incluído automaticamente da solicitação."
                );
            }
        }

        Protocolo salvoProtocolo = protocoloRepository.save(novoProtocolo);
        System.out.println("Protocolo criado automaticamente para SolicitacaoLocal ID: " + salvoProtocolo.getSolicitacao().getId());
        return salvoProtocolo;
    }

    @Transactional
    public Protocolo adicionarServicoExistenteOuNovoAoProtocolo(
            Long protocoloId,
            Long servicoExistentePrincipalId,
            String novoServicoNome,
            BigDecimal novoServicoPreco,
            String observacoesServico
    ) {
        Protocolo protocolo = protocoloRepository.findById(protocoloId)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado para adicionar serviço."));

        Long idServicoSnapshot = null;
        String codigoServicoSnapshot = null;
        String nomeServicoSnapshot = null;
        BigDecimal precoServicoSnapshot = null;

        if (servicoExistentePrincipalId != null) {
            Servico servicoPrincipal = servicoPrincipalService.buscarServicoPorId(servicoExistentePrincipalId)
                    .orElseThrow(() -> new RuntimeException("Serviço existente com ID " + servicoExistentePrincipalId + " não encontrado no módulo principal."));

            idServicoSnapshot = servicoPrincipal.getId();
            codigoServicoSnapshot = servicoPrincipal.getCodigo();
            nomeServicoSnapshot = servicoPrincipal.getNome();
            precoServicoSnapshot = servicoPrincipal.getPreco();

        } else if (novoServicoNome != null && !novoServicoNome.trim().isEmpty() && novoServicoPreco != null) {
            String codigoDinamico = "DYN-" + System.nanoTime();
            Servico novoServicoNoModuloPrincipal = new Servico(codigoDinamico, novoServicoNome, novoServicoPreco);

            Servico servicoSalvoPrincipal = servicoPrincipalService.criarServico(novoServicoNoModuloPrincipal);

            idServicoSnapshot = servicoSalvoPrincipal.getId();
            codigoServicoSnapshot = servicoSalvoPrincipal.getCodigo();
            nomeServicoSnapshot = servicoSalvoPrincipal.getNome();
            precoServicoSnapshot = servicoSalvoPrincipal.getPreco();
        } else {
            throw new IllegalArgumentException("Para adicionar um serviço, selecione um serviço existente ou forneça nome e preço para um novo.");
        }

        protocolo.adicionarServico(idServicoSnapshot, codigoServicoSnapshot, nomeServicoSnapshot, precoServicoSnapshot, observacoesServico);

        return protocoloRepository.save(protocolo);
    }

    @Transactional
    public Protocolo confirmarProtocolo(Long id) {
        Protocolo protocoloExistente = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado para confirmação!"));

        if (protocoloExistente.getStatusProtocolo() == Protocolo.Status.CONFIRMADO) {
            System.out.println("Protocolo " + id + " já está CONFIRMADO. E-mail de confirmação não será reenviado.");
            return protocoloExistente;
        }

        if (protocoloExistente.getStatusProtocolo() == Protocolo.Status.CANCELADO ||
                protocoloExistente.getStatusProtocolo().name().startsWith("CONCLUIDO_")) {
            throw new IllegalArgumentException("Protocolo não pode ser confirmado de um status final (Cancelado/Concluído).");
        }

        protocoloExistente.setStatusProtocolo(Protocolo.Status.CONFIRMADO);
        Protocolo protocoloAtualizado = protocoloRepository.save(protocoloExistente);

        String toEmail = protocoloAtualizado.getSolicitacao().getEmail();
        String clientName = protocoloAtualizado.getSolicitacao().getNome();
        String serviceDetails = protocoloAtualizado.getServicosProtocolo().stream()
                .map(item -> item.getNomeServico())
                .collect(Collectors.joining(", "));
        String appointmentDate = protocoloAtualizado.getDataAgendadaProtocolo() != null ?
                protocoloAtualizado.getDataAgendadaProtocolo().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
        String appointmentTime = protocoloAtualizado.getSolicitacao().getDataAgendada() != null ?
                protocoloAtualizado.getSolicitacao().getDataAgendada().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) : "N/A";
        String protocolNumber = protocoloAtualizado.getNumeroProtocolo();

        notificacaoService.sendAppointmentConfirmedByMechanicEmail( // Usando o novo método
                toEmail,
                clientName,
                serviceDetails,
                appointmentDate,
                appointmentTime,
                protocolNumber
        );
        System.out.println("E-mail de 'Agendamento Confirmado' disparado para o protocolo: " + protocolNumber);

        return protocoloAtualizado;
    }

    @Transactional
    public Protocolo concluirProtocolo(Long id, Protocolo.Status statusConclusao) {
        if (statusConclusao != Protocolo.Status.CONCLUIDO_AGUARDANDO_RETIDADA_CLIENTE &&
                statusConclusao != Protocolo.Status.CONCLUIDO_FINALIZADO) {
            throw new IllegalArgumentException("O status de conclusão deve ser CONCLUIDO_AGUARDANDO_RETIRADA_CLIENTE ou CONCLUIDO_FINALIZADO.");
        }

        Protocolo protocoloExistente = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado para conclusão!"));

        if (protocoloExistente.getStatusProtocolo() == Protocolo.Status.CANCELADO) {
            throw new IllegalArgumentException("Protocolo cancelado não pode ser concluído.");
        }

        protocoloExistente.setStatusProtocolo(statusConclusao);
        Protocolo protocoloAtualizado = protocoloRepository.save(protocoloExistente);

        if (statusConclusao == Protocolo.Status.CONCLUIDO_AGUARDANDO_RETIDADA_CLIENTE) {
            String toEmail = protocoloAtualizado.getSolicitacao().getEmail();
            String clientName = protocoloAtualizado.getSolicitacao().getNome();
            String vehicleDetails = String.format("%s %s (%s) - Placa: %s",
                    protocoloAtualizado.getSolicitacao().getMarca(),
                    protocoloAtualizado.getSolicitacao().getModelo(),
                    protocoloAtualizado.getSolicitacao().getAno(),
                    protocoloAtualizado.getSolicitacao().getPlaca());
            String serviceDetails = protocoloAtualizado.getServicosProtocolo().stream()
                    .map(item -> item.getNomeServico())
                    .collect(Collectors.joining(", "));
            String protocolNumber = protocoloAtualizado.getNumeroProtocolo();

            notificacaoService.sendServiceConcludedWaitingPickupEmail(
                    toEmail,
                    clientName,
                    vehicleDetails,
                    serviceDetails,
                    protocolNumber
            );
            System.out.println("E-mail de 'Aguardando Retirada' disparado para o protocolo: " + protocolNumber);
        }

        return protocoloAtualizado;
    }

    @Transactional
    public Protocolo cancelarProtocolo(Long id, String mechanicObservation) { // Adicionado o parâmetro mechanicObservation
        Protocolo protocoloExistente = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado para cancelamento!"));

        if (protocoloExistente.getStatusProtocolo() == Protocolo.Status.CONCLUIDO_AGUARDANDO_RETIDADA_CLIENTE ||
                protocoloExistente.getStatusProtocolo() == Protocolo.Status.CONCLUIDO_FINALIZADO) {
            throw new IllegalArgumentException("Protocolo já concluído não pode ser cancelado.");
        }

        if (protocoloExistente.getStatusProtocolo() != Protocolo.Status.CANCELADO) {
            String toEmail = protocoloExistente.getSolicitacao().getEmail();
            String clientName = protocoloExistente.getSolicitacao().getNome();
            String protocolNumber = protocoloExistente.getNumeroProtocolo();
            String vehicleDetails = String.format("%s %s (%s) - Placa: %s",
                    protocoloExistente.getSolicitacao().getMarca(),
                    protocoloExistente.getSolicitacao().getModelo(),
                    protocoloExistente.getSolicitacao().getAno(),
                    protocoloExistente.getSolicitacao().getPlaca());
            String reason = "O agendamento/serviço foi cancelado pela oficina.";

            notificacaoService.sendProtocolCanceledEmail(
                    toEmail,
                    clientName,
                    protocolNumber,
                    vehicleDetails,
                    reason,
                    mechanicObservation // Passando a observação do mecânico
            );
            System.out.println("E-mail de 'Protocolo Cancelado' disparado para o protocolo: " + protocolNumber);
        }

        protocoloExistente.setStatusProtocolo(Protocolo.Status.CANCELADO);
        return protocoloRepository.save(protocoloExistente);
    }

    @Transactional
    public List<Protocolo> listarTodosProtocolos() {
        return protocoloRepository.findAllByOrderByDataProtocoloDesc();
    }

    public Optional<Protocolo> buscarProtocoloPorId(Long id) {
        return protocoloRepository.findById(id);
    }

    @Transactional
    public Protocolo atualizarStatusProtocolo(Long id, Protocolo.Status novoStatus) {
        Protocolo protocoloExistente = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado para atualização!"));

        if (novoStatus == Protocolo.Status.CANCELADO || novoStatus.name().startsWith("CONCLUIDO_") || novoStatus == Protocolo.Status.CONFIRMADO) {
            throw new IllegalArgumentException("Status 'CANCELADO', 'CONCLUIDO', ou 'CONFIRMADO' devem ser usados através de funções específicas.");
        }

        protocoloExistente.setStatusProtocolo(novoStatus);
        return protocoloRepository.save(protocoloExistente);
    }

    public Optional<Protocolo> buscarProtocoloPorNumero(String numeroProtocolo) {
        return protocoloRepository.findByNumeroProtocolo(numeroProtocolo);
    }

    @Transactional
    public void removerServicoItem(Long protocoloId, Long itemId) {
        Protocolo protocolo = protocoloRepository.findById(protocoloId)
                .orElseThrow(() -> new IllegalArgumentException("Protocolo não encontrado."));

        Optional<ProtocoloServicoItem> itemParaRemover = protocolo.getServicosProtocolo().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();

        if (itemParaRemover.isPresent()) {
            protocolo.getServicosProtocolo().remove(itemParaRemover.get());
        } else {
            throw new IllegalArgumentException("Serviço item com ID " + itemId + " não encontrado no protocolo " + protocoloId + ".");
        }
    }
}