package com.buenosautos.buenosautosagendamento.protocolo.service;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buenosautos.buenosautosagendamento.protocolo.model.Protocolo;
import com.buenosautos.buenosautosagendamento.protocolo.model.ProtocoloServicoItem; // Importe esta classe
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
            List<ServicoLocal> servicosEncontrados = servicoLocalRepository.findAllById(servicoIdsParaProtocolo);
            for (ServicoLocal servico : servicosEncontrados) {
                novoProtocolo.adicionarServico(servico, "Serviço incluído automaticamente da solicitação.");
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

        ServicoLocal servicoLocalParaAdicionar;

        if (servicoExistentePrincipalId != null) {
            servicoLocalParaAdicionar = servicoLocalRepository.findById(servicoExistentePrincipalId)
                .orElseThrow(() -> new RuntimeException("Serviço existente com ID " + servicoExistentePrincipalId + " não encontrado na cópia local."));
            
            if (servicoLocalParaAdicionar.getStatus() == ServicoLocal.Status.DESATIVADO) {
                throw new IllegalArgumentException("Não é possível adicionar um serviço desativado ao protocolo.");
            }

        } else if (novoServicoNome != null && !novoServicoNome.trim().isEmpty() && novoServicoPreco != null) {
            String codigoDinamico = "DYN-" + System.nanoTime(); 
            Servico novoServicoNoModuloPrincipal = new Servico(codigoDinamico, novoServicoNome, novoServicoPreco);
            
            Servico servicoSalvoPrincipal = servicoPrincipalService.criarServico(novoServicoNoModuloPrincipal);

            servicoLocalParaAdicionar = servicoLocalRepository.findById(servicoSalvoPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Erro interno: Cópia local do serviço dinâmico não encontrada após criação."));
        } else {
            throw new IllegalArgumentException("Para adicionar um serviço, selecione um serviço existente ou forneça nome e preço para um novo.");
        }

        protocolo.adicionarServico(servicoLocalParaAdicionar, observacoesServico);

        return protocoloRepository.save(protocolo);
    }
    
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
        
        if (novoStatus == Protocolo.Status.CANCELADO) {
            throw new IllegalArgumentException("Status 'CANCELADO' deve ser usado apenas através da função de cancelamento específica.");
        }

        protocoloExistente.setStatusProtocolo(novoStatus);
        return protocoloRepository.save(protocoloExistente);
    }

    @Transactional
    public Protocolo cancelarProtocolo(Long id) {
        Protocolo protocoloExistente = protocoloRepository.findById(id)
                                        .orElseThrow(() -> new RuntimeException("Protocolo não encontrado para cancelamento!"));
        
        if (protocoloExistente.getStatusProtocolo() == Protocolo.Status.CONCLUIDO) {
            throw new IllegalArgumentException("Protocolo já concluído não pode ser cancelado.");
        }

        protocoloExistente.setStatusProtocolo(Protocolo.Status.CANCELADO);
        return protocoloRepository.save(protocoloExistente);
    }
    
    public Optional<Protocolo> buscarProtocoloPorNumero(String numeroProtocolo) {
        return protocoloRepository.findByNumeroProtocolo(numeroProtocolo);
    }

    // NOVO: Método para remover um ProtocoloServicoItem
    @Transactional
    public void removerServicoItem(Long protocoloId, Long itemId) {
        Protocolo protocolo = protocoloRepository.findById(protocoloId)
            .orElseThrow(() -> new IllegalArgumentException("Protocolo não encontrado."));

        // Encontra o item de serviço na lista do protocolo
        Optional<ProtocoloServicoItem> itemParaRemover = protocolo.getServicosProtocolo().stream()
            .filter(item -> item.getId().equals(itemId))
            .findFirst();

        if (itemParaRemover.isPresent()) {
            // Remove o item da coleção. Graças a orphanRemoval=true no @OneToMany,
            // o JPA irá deletar este item do banco de dados quando o protocolo for salvo.
            protocolo.getServicosProtocolo().remove(itemParaRemover.get());
            // Não precisa chamar save() explicitamente no protocoloRepository aqui,
            // pois a operação é transacional e a mudança na coleção gerenciada será sincronizada.
            // No entanto, para garantir que o flush ocorra e o item seja deletado na transação,
            // você pode chamar protocoloRepository.save(protocolo); se quiser forçar o flush,
            // mas o @Transactional já cuida da sincronização.
            // Para maior clareza e forçar a operação, pode-se salvar.
            // protocoloRepository.save(protocolo); // Opcional, dependendo do contexto.
            System.out.println("Serviço item ID " + itemId + " removido do protocolo " + protocoloId);
        } else {
            throw new IllegalArgumentException("Serviço item com ID " + itemId + " não encontrado no protocolo " + protocoloId + ".");
        }
    }
}