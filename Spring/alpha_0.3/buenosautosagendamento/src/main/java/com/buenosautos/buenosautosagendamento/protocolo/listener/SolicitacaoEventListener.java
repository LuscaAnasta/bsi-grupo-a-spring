package com.buenosautos.buenosautosagendamento.protocolo.listener;

import com.buenosautos.buenosautosagendamento.solicitacao.event.SolicitacaoEvent; // Evento do módulo Solicitacao
import com.buenosautos.buenosautosagendamento.protocolo.model.SolicitacaoLocal;
import com.buenosautos.buenosautosagendamento.protocolo.service.ProtocoloService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class SolicitacaoEventListener {

    @Autowired
    private ProtocoloService protocoloService;

    @EventListener
    public void handleSolicitacaoEvent(SolicitacaoEvent event) {
        System.out.println("<<< Protocolo.SolicitacaoEventListener: Evento de Solicitacao recebido: " + event.getType() + " para ID: " + event.getId());

        // Processar apenas eventos de CRIAÇÃO para criar o protocolo
        if ("CREATED".equals(event.getType())) {
            // 1. Criar a instância de SolicitacaoLocal a partir dos dados do evento
            SolicitacaoLocal solicitacaoLocal = new SolicitacaoLocal(
                event.getId(),
                event.getClienteNome(),
                event.getClienteCpf(),
                event.getClienteEmail(),
                event.getClienteTelefone(),
                event.getVeiculoMarca(),
                event.getVeiculoModelo(),
                event.getVeiculoAno(),
                event.getVeiculoPlaca(), // <<--- AQUI: Passando a placa do evento
                event.getDataSolicitacao(),
                event.getDataAgendada()
            );

            // 2. Chamar o serviço de Protocolo para processar a SolicitacaoLocal e criar o Protocolo
            try {
                protocoloService.processarSolicitacaoEventAndCreateProtocolo(solicitacaoLocal, event.getServicoIds());
            } catch (Exception e) {
                System.err.println("Erro ao processar Solicitação Event e criar Protocolo para ID " + event.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else if ("UPDATED".equals(event.getType())) {
            System.out.println("Solicitacao Evento ATUALIZADO (ignorado para criação de protocolo): " + event.getId());
            // Se precisar que atualizações na Solicitacao original impactem o Protocolo, implemente aqui.
            // Ex: atualizar dados da SolicitacaoLocal ou do Protocolo.
        } else if ("DELETED".equals(event.getType())) {
            System.out.println("Solicitacao Evento DELETADO (se aplicável, para lidar com Protocolo): " + event.getId());
            // Se precisar lidar com deleção lógica da Solicitação (ex: cancelar Protocolos associados), implemente aqui.
        } else {
            System.out.println("Tipo de evento desconhecido: " + event.getType());
        }
    }
}