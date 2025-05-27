package com.buenosautos.buenosautosagendamento.servico.service;

import com.buenosautos.buenosautosagendamento.servico.event.ServicoEvent;
import com.buenosautos.buenosautosagendamento.servico.model.Servico;
import com.buenosautos.buenosautosagendamento.servico.repository.ServicoRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Adicione esta importação

@Service
public class ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional // Adicione @Transactional para garantir que a atualização seja atômica
    public Servico criarServico(Servico servico) {
        Servico salvo = servicoRepository.save(servico);
        eventPublisher.publishEvent(new ServicoEvent(salvo.getId(), salvo.getCodigo(), salvo.getNome(), salvo.getPreco(), "CREATED"));
        System.out.println(">>> ServicoService: Evento 'CREATED' publicado para Servico ID: " + salvo.getId() + " - " + salvo.getNome()); // Adicionado para depuração
        return salvo;
    }

    @Transactional // <--- Adicione @Transactional aqui
    public Servico atualizarServico(Long id, Servico servicoAtualizado) {
        Servico servicoExistente = servicoRepository.findById(id)
                                            .orElseThrow(() -> new RuntimeException("Serviço não encontrado para atualização com ID: " + id));

        // --- CORREÇÃO: DESCOMENTE E IMPLEMENTE A ATUALIZAÇÃO DOS CAMPOS ---
        servicoExistente.setCodigo(servicoAtualizado.getCodigo());
        servicoExistente.setNome(servicoAtualizado.getNome());
        servicoExistente.setPreco(servicoAtualizado.getPreco());
        // ------------------------------------------------------------------

        Servico salvo = servicoRepository.save(servicoExistente);
        eventPublisher.publishEvent(new ServicoEvent(salvo.getId(), salvo.getCodigo(), salvo.getNome(), salvo.getPreco(), "UPDATED"));
        System.out.println(">>> ServicoService: Evento 'UPDATED' publicado para Servico ID: " + salvo.getId() + " - " + salvo.getNome()); // Adicionado para depuração
        return salvo;
    }

    @Transactional // <--- Adicione @Transactional aqui
    public void deletarServico(Long id) {
        if (servicoRepository.existsById(id)) {
            servicoRepository.deleteById(id);
            eventPublisher.publishEvent(new ServicoEvent(id, null, null, null, "DELETED"));
            System.out.println(">>> ServicoService: Evento 'DELETED' publicado para Servico ID: " + id); // Adicionado para depuração
        } else {
            throw new RuntimeException("Serviço com ID " + id + " não encontrado para deleção.");
        }
    }

    public List<Servico> listarTodosServicos() {
        return servicoRepository.findAll();
    }

    public Optional<Servico> buscarServicoPorId(Long id) {
        return servicoRepository.findById(id);
    }
}