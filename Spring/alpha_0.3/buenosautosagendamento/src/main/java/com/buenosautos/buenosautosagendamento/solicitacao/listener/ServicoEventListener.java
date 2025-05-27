package com.buenosautos.buenosautosagendamento.solicitacao.listener;

import com.buenosautos.buenosautosagendamento.servico.event.ServicoEvent; // Evento do microsserviço Servico
import com.buenosautos.buenosautosagendamento.solicitacao.model.ServicoLocal; // Entidade ServicoLocal deste microsserviço
import com.buenosautos.buenosautosagendamento.solicitacao.repository.ServicoLocalRepository; // Repositório local
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service("ServicoEventListenerSolicitacao") 
public class ServicoEventListener {

    @Autowired
    private ServicoLocalRepository servicoLocalRepository;

    @EventListener // Anotação para escutar eventos internos do Spring
    public void handleServicoEvent(ServicoEvent event) { // O método não retorna Consumer
        System.out.println("Evento de Serviço recebido (interno): " + event);

        if ("CREATED".equals(event.getType()) || "UPDATED".equals(event.getType())) {
            // Filtra apenas serviços com código começando com 'C'
            if (event.getCodigo() != null && event.getCodigo().startsWith("C")) {
                ServicoLocal servicoLocal = new ServicoLocal(
                    event.getId(),
                    event.getCodigo(),
                    event.getNome(),
                    event.getPreco()
                );
                servicoLocalRepository.save(servicoLocal);
                System.out.println("Serviço Local CRIADO/ATUALIZADO (filtrado): " + servicoLocal.getId());
            } else {
                System.out.println("Serviço ignorado (código não começa com 'C'): " + event.getCodigo());
                // Desativar servico caso mude o codigo
                // servicoLocalRepository.findById(event.getId()).ifPresent(s -> {
                //     if (s.getStatus() == ServicoLocal.Status.ATIVO) {
                //         s.setStatus(ServicoLocal.Status.DESATIVADO);
                //         servicoLocalRepository.save(s);
                //         System.out.println("Serviço Local desativado por filtro de código: " + s.getId());
                //     }
                // });
            }
        } else if ("DELETED".equals(event.getType())) {
            servicoLocalRepository.findById(event.getId()).ifPresent(s -> {
                s.setStatus(ServicoLocal.Status.DESATIVADO);
                servicoLocalRepository.save(s);
                System.out.println("Serviço Local MARCADO COMO DESATIVADO (por deleção original): " + s.getId());
            });
        } else {
            System.out.println("Tipo de evento desconhecido: " + event.getType());
        }
    };
}
