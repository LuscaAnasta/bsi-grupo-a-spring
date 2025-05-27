package com.buenosautos.buenosautosagendamento.solicitacao.controller;

import com.buenosautos.buenosautosagendamento.solicitacao.dto.SolicitacaoFormDTO;
import com.buenosautos.buenosautosagendamento.solicitacao.model.ServicoLocal;
import com.buenosautos.buenosautosagendamento.solicitacao.model.Solicitacao;
import com.buenosautos.buenosautosagendamento.solicitacao.repository.ServicoLocalRepository;
import com.buenosautos.buenosautosagendamento.solicitacao.service.SolicitacaoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;


import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/solicitacoes")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @Autowired
    @Qualifier("solicitacaoServicoLocalRepository")
    private ServicoLocalRepository solicitacaoServicoLocalRepository; // Renomeado para consistencia

    // Remova @Autowired de ClienteRepository e VeiculoRepository daqui
    // @Autowired
    // private ClienteRepository clienteRepository;
    // @Autowired
    // private VeiculoRepository veiculoRepository;


    @GetMapping("/nova")
    public String exibirFormularioSolicitacao(Model model) {
        model.addAttribute("solicitacaoForm", new SolicitacaoFormDTO());

        List<ServicoLocal> servicosDisponiveis = solicitacaoServicoLocalRepository.findAll().stream()
            .filter(s -> s.getStatus() == ServicoLocal.Status.ATIVO)
            .filter(s -> s.getCodigo() != null && s.getCodigo().startsWith("C")) // Filtro de código
            .collect(Collectors.toList());

        if (servicosDisponiveis.isEmpty()) {
            model.addAttribute("erroServicos", "Nenhum serviço disponível que atenda aos critérios.");
        }
        model.addAttribute("servicosDisponiveis", servicosDisponiveis);

        return "formulario-solicitacao";
    }

    @PostMapping("/nova")
    public String processarFormularioSolicitacao(
            @Valid @ModelAttribute("solicitacaoForm") SolicitacaoFormDTO solicitacaoFormDTO,
            BindingResult bindingResult,
            Model model
        ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("servicosDisponiveis", solicitacaoServicoLocalRepository.findAll().stream() // Usar o nome correto
                .filter(s -> s.getStatus() == ServicoLocal.Status.ATIVO)
                .filter(s -> s.getCodigo() != null && s.getCodigo().startsWith("C"))
                .collect(Collectors.toList()));
            return "formulario-solicitacao";
        }

        System.out.println("Dados recebidos do formulário: " + solicitacaoFormDTO);

        try {
            // Chamada simplificada ao serviço, passando apenas o DTO
            solicitacaoService.criarSolicitacao(solicitacaoFormDTO);

            model.addAttribute("mensagemSucesso", "Solicitação enviada com sucesso!");
            model.addAttribute("solicitacaoForm", new SolicitacaoFormDTO()); // Limpa o formulário
            return "formulario-solicitacao";
        } catch (Exception e) {
            System.err.println("Erro ao processar solicitação: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("mensagemErro", "Ocorreu um erro ao enviar sua solicitação. Detalhes: " + e.getMessage());
            model.addAttribute("servicosDisponiveis", solicitacaoServicoLocalRepository.findAll().stream() // Usar o nome correto
                .filter(s -> s.getStatus() == ServicoLocal.Status.ATIVO)
                .filter(s -> s.getCodigo() != null && s.getCodigo().startsWith("C"))
                .collect(Collectors.toList()));
            return "formulario-solicitacao";
        }
        
    }
    @GetMapping("/listar")
    public String listarSolicitacoes(Model model) {
        List<Solicitacao> solicitacoes = solicitacaoService.listarSolicitacoesMaisAtuais();
        model.addAttribute("solicitacoes", solicitacoes);
        return "solicitacoes/listar-solicitacoes"; // Retorna o template HTML
    }
}