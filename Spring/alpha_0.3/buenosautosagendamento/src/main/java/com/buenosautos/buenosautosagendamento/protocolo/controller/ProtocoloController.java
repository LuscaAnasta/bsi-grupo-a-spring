package com.buenosautos.buenosautosagendamento.protocolo.controller;

import com.buenosautos.buenosautosagendamento.protocolo.model.Protocolo;
import com.buenosautos.buenosautosagendamento.protocolo.model.ServicoLocal;
import com.buenosautos.buenosautosagendamento.protocolo.repository.ServicoLocalRepository;
import com.buenosautos.buenosautosagendamento.protocolo.service.ProtocoloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.util.Arrays; // Necessário para usar Arrays.stream

@Controller
@RequestMapping("/protocolos")
public class ProtocoloController {

    @Autowired
    private ProtocoloService protocoloService;
    
    @Autowired
    @Qualifier("protocoloServicoLocalRepository") // Garante que é o repositório correto
    private ServicoLocalRepository protocoloServicoLocalRepository;

    // Endpoint para listar todos os protocolos
    @GetMapping("/listar")
    public String listarProtocolos(Model model) {
        List<Protocolo> protocolos = protocoloService.listarTodosProtocolos();
        model.addAttribute("protocolos", protocolos);
        return "protocolos/listar-protocolos";
    }

    // Endpoint para exibir os detalhes de um protocolo e formulário de edição de status
    @GetMapping("/detalhes/{id}")
    public String exibirDetalhesProtocolo(@PathVariable Long id, Model model) {
        Protocolo protocolo = protocoloService.buscarProtocoloPorId(id)
                                             .orElseThrow(() -> new RuntimeException("Protocolo não encontrado!"));
        model.addAttribute("protocolo", protocolo);

        // Opções de status para o dropdown de "Alterar Status"
        // Filtra CANCELADO e os status de CONCLUSAO_* porque eles terão botões/ações específicas
        List<Protocolo.Status> statusOptions = Arrays.stream(Protocolo.Status.values())
                                                     .filter(s -> s != Protocolo.Status.CANCELADO)
                                                     .filter(s -> !s.name().startsWith("CONCLUIDO_")) // Filtra todos os CONCLUIDO_
                                                     .collect(Collectors.toList());
        model.addAttribute("statusOptions", statusOptions);

        // Adiciona a lista de serviços locais ativos para o dropdown de "adicionar serviço existente"
        List<ServicoLocal> servicosDisponiveis = protocoloServicoLocalRepository.findAll().stream()
            .filter(s -> s.getStatus() == ServicoLocal.Status.ATIVO)
            .collect(Collectors.toList());
        model.addAttribute("servicosDisponiveis", servicosDisponiveis);

        return "protocolos/detalhes-protocolo";
    }

    // Endpoint para salvar alterações de status (exclui CANCELAR e CONCLUIR)
    @PostMapping("/salvar-status/{id}")
    public String salvarStatusProtocolo(@PathVariable Long id, 
                                        @RequestParam Protocolo.Status novoStatus, 
                                        RedirectAttributes redirectAttributes) {
        try {
            // Este endpoint não deve ser usado para Cancelar ou Concluir.
            // O serviço já tem validação, mas podemos ser explícitos aqui também.
            if (novoStatus == Protocolo.Status.CANCELADO || novoStatus.name().startsWith("CONCLUIDO_")) {
                throw new IllegalArgumentException("Status 'CANCELADO' ou 'CONCLUIDO' devem ser usados através de funções específicas.");
            }
            protocoloService.atualizarStatusProtocolo(id, novoStatus);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Status do protocolo atualizado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro de validação: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Erro ao atualizar status: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensagemErro", "Ocorreu um erro inesperado ao atualizar status: " + e.getMessage());
        }
        return "redirect:/protocolos/detalhes/{id}";
    }

    // Endpoint para cancelar protocolo (método já existente)
    @PostMapping("/cancelar/{id}")
    public String cancelarProtocolo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            protocoloService.cancelarProtocolo(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Protocolo cancelado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro de validação: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Erro ao cancelar protocolo: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensagemErro", "Ocorreu um erro inesperado ao cancelar protocolo: " + e.getMessage());
        }
        return "redirect:/protocolos/detalhes/{id}";
    }
    
    // NOVO MÉTODO: Endpoint para concluir protocolo (chamará o ProtocoloService.concluirProtocolo)
    @PostMapping("/concluir/{id}")
    public String concluirProtocolo(@PathVariable Long id, 
                                    @RequestParam Protocolo.Status statusConclusao, // Será CONCLUIDO_AGUARDANDO_RETIDADA_CLIENTE ou CONCLUIDO_FINALIZADO
                                    RedirectAttributes redirectAttributes) {
        try {
            protocoloService.concluirProtocolo(id, statusConclusao); // Chama o novo método do serviço
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Protocolo concluído com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro de validação: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Erro ao concluir protocolo: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensagemErro", "Ocorreu um erro inesperado ao concluir protocolo: " + e.getMessage());
        }
        return "redirect:/protocolos/detalhes/{id}";
    }
    
    // NOVO: Endpoint para adicionar serviço diretamente na página de detalhes
    @PostMapping("/adicionar-servico-direto/{protocoloId}")
    public String adicionarServicoDiretoAoProtocolo(
        @PathVariable Long protocoloId,
        @RequestParam(value = "servicoExistenteId", required = false) Long servicoExistenteId,
        @RequestParam(value = "novoServicoNome", required = false) String novoServicoNome,
        @RequestParam(value = "novoServicoPreco", required = false) BigDecimal novoServicoPreco,
        @RequestParam(value = "observacoesNovoServico", required = false) String observacoes,
        RedirectAttributes redirectAttributes
    ) {
        try {
            if (servicoExistenteId == null && (novoServicoNome == null || novoServicoNome.trim().isEmpty() || novoServicoPreco == null)) {
                throw new IllegalArgumentException("Selecione um serviço existente ou forneça nome e preço para um novo serviço.");
            }

            protocoloService.adicionarServicoExistenteOuNovoAoProtocolo(
                protocoloId,
                servicoExistenteId,
                novoServicoNome,
                novoServicoPreco,
                observacoes
            );
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Serviço adicionado ao protocolo com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro de validação: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao adicionar serviço ao protocolo: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensagemErro", "Ocorreu um erro inesperado ao adicionar serviço: " + e.getMessage());
        }
        return "redirect:/protocolos/detalhes/{protocoloId}";
    }

    // NOVO: Endpoint para remover um ProtocoloServicoItem
    @PostMapping("/remover-servico-item/{protocoloId}/{itemId}")
    public String removerServicoItemDoProtocolo(
        @PathVariable Long protocoloId,
        @PathVariable Long itemId,
        RedirectAttributes redirectAttributes
    ) {
        try {
            protocoloService.removerServicoItem(protocoloId, itemId);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Serviço removido do protocolo com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao remover serviço do protocolo: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensagemErro", "Ocorreu um erro inesperado ao remover o serviço: " + e.getMessage());
        }
        return "redirect:/protocolos/detalhes/{protocoloId}";
    }
}