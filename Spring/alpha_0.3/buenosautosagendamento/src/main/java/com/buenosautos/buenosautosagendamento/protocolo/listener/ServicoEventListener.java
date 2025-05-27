package com.buenosautos.buenosautosagendamento.protocolo.listener;

import com.buenosautos.buenosautosagendamento.servico.event.ServicoEvent; // Evento do módulo Servico
import com.buenosautos.buenosautosagendamento.protocolo.model.ServicoLocal; // A ServicoLocal deste módulo (Protocolo)
import com.buenosautos.buenosautosagendamento.protocolo.repository.ServicoLocalRepository; // Repositório deste módulo (Protocolo)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service("ServicoEventListenerProtocolo") 
public class ServicoEventListener {

    @Autowired
    private ServicoLocalRepository servicoLocalRepository;

    @EventListener
    public void handleServicoEvent(ServicoEvent event) {
        System.out.println("<<< Protocolo.ServicoEventListener: Evento de Serviço recebido: " + event.getType() + " para ID: " + event.getId());

        switch (event.getType()) {
            case "CREATED":
            case "UPDATED":
                // Sem filtro de código aqui, salva/atualiza TODOS os serviços
                ServicoLocal servicoLocal = new ServicoLocal(
                    event.getId(),
                    event.getCodigo(),
                    event.getNome(),
                    event.getPreco()
                );
                servicoLocalRepository.save(servicoLocal);
                System.out.println("Serviço Local CRIADO/ATUALIZADO no Protocolo: " + servicoLocal.getId() + " - " + servicoLocal.getNome());
                break;
            case "DELETED":
                // Desativa a cópia local, independentemente do código
                servicoLocalRepository.findById(event.getId()).ifPresent(s -> {
                    s.setStatus(ServicoLocal.Status.DESATIVADO);
                    servicoLocalRepository.save(s);
                    System.out.println("Serviço Local MARCADO COMO DESATIVADO no Protocolo: " + s.getId());
                });
                break;
            default:
                System.out.println("Tipo de evento desconhecido: " + event.getType());
        }
    }
}