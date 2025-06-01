package com.buenosautos.buenosautosagendamento.solicitacao.controller;

import com.buenosautos.buenosautosagendamento.solicitacao.dto.SolicitacaoFormDTO;
import com.buenosautos.buenosautosagendamento.solicitacao.model.ServicoLocal;
import com.buenosautos.buenosautosagendamento.solicitacao.model.Solicitacao;
import com.buenosautos.buenosautosagendamento.solicitacao.repository.ServicoLocalRepository;
import com.buenosautos.buenosautosagendamento.solicitacao.service.SolicitacaoService;
import com.buenosautos.buenosautosagendamento.disponibilidade.service.DisponibilidadeService;
import com.buenosautos.buenosautosagendamento.disponibilidade.dto.DiaDisponivelDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/solicitacoes")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @Autowired
    @Qualifier("solicitacaoServicoLocalRepository")
    private ServicoLocalRepository solicitacaoServicoLocalRepository;

    @Autowired
    private DisponibilidadeService disponibilidadeService;

    @GetMapping("/nova")
    public String exibirFormularioSolicitacao(Model model) {
        model.addAttribute("solicitacaoForm", new SolicitacaoFormDTO());

        List<DiaDisponivelDTO> diasComHorariosDisponiveis = disponibilidadeService.listarDiasComHorariosDisponiveisParaAgendamento();
        model.addAttribute("diasComHorariosDisponiveis", diasComHorariosDisponiveis);

        List<ServicoLocal> servicosDisponiveis = solicitacaoServicoLocalRepository.findAll().stream()
            .filter(s -> s.getStatus() == ServicoLocal.Status.ATIVO)
            .filter(s -> s.getCodigo() != null && s.getCodigo().startsWith("C"))
            .collect(Collectors.toList());

        if (servicosDisponiveis.isEmpty()) {
            model.addAttribute("erroServicos", "Nenhum serviço disponível que atenda aos critérios.");
        }
        model.addAttribute("servicosDisponiveis", servicosDisponiveis);

        // ALTERADO AQUI: aponta para o novo diretório
        return "solicitacoes/formulario-solicitacao";
    }

    @GetMapping("/nova-mecanico")
    public String exibirFormularioSolicitacaoMecanico(Model model) {
        model.addAttribute("solicitacaoForm", new SolicitacaoFormDTO());

        List<DiaDisponivelDTO> diasComHorariosDisponiveis = disponibilidadeService.listarDiasComHorariosDisponiveisParaAgendamento();
        model.addAttribute("diasComHorariosDisponiveis", diasComHorariosDisponiveis);

        List<ServicoLocal> servicosDisponiveis = solicitacaoServicoLocalRepository.findAll().stream()
            .filter(s -> s.getStatus() == ServicoLocal.Status.ATIVO)
            .filter(s -> s.getCodigo() != null && s.getCodigo().startsWith("C"))
            .collect(Collectors.toList());

        if (servicosDisponiveis.isEmpty()) {
            model.addAttribute("erroServicos", "Nenhum serviço disponível que atenda aos critérios.");
        }
        model.addAttribute("servicosDisponiveis", servicosDisponiveis);

        // ALTERADO AQUI: aponta para o novo diretório
        return "solicitacoes/formulario-solicitacao-mecanico";
    }

    @PostMapping("/nova")
    public String processarFormularioSolicitacao(
            @Valid @ModelAttribute("solicitacaoForm") SolicitacaoFormDTO solicitacaoFormDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
        ) {
        if (bindingResult.hasErrors()) {
            List<ServicoLocal> servicosDisponiveis = solicitacaoServicoLocalRepository.findAll().stream()
                .filter(s -> s.getStatus() == ServicoLocal.Status.ATIVO)
                .filter(s -> s.getCodigo() != null && s.getCodigo().startsWith("C"))
                .collect(Collectors.toList());
            model.addAttribute("servicosDisponiveis", servicosDisponiveis);
            
            List<DiaDisponivelDTO> diasComHorariosDisponiveis = disponibilidadeService.listarDiasComHorariosDisponiveisParaAgendamento();
            model.addAttribute("diasComHorariosDisponiveis", diasComHorariosDisponiveis);

            // ALTERADO AQUI: aponta para o novo diretório
            return "solicitacoes/formulario-solicitacao";
        }

        try {
            LocalDate dataAgendadaDia = solicitacaoFormDTO.getDataAgendadaDia();
            disponibilidadeService.consultarDisponibilidadeParaAgendamento(dataAgendadaDia)
                .orElseThrow(() -> new IllegalArgumentException("O dia selecionado não está disponível para agendamento."));
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("dataAgendadaDia", "disponibilidade.indisponivel", e.getMessage());
            List<ServicoLocal> servicosDisponiveis = solicitacaoServicoLocalRepository.findAll().stream()
                .filter(s -> s.getStatus() == ServicoLocal.Status.ATIVO)
                .filter(s -> s.getCodigo() != null && s.getCodigo().startsWith("C"))
                .collect(Collectors.toList());
            model.addAttribute("servicosDisponiveis", servicosDisponiveis);
            
            List<DiaDisponivelDTO> diasComHorariosDisponiveis = disponibilidadeService.listarDiasComHorariosDisponiveisParaAgendamento();
            model.addAttribute("diasComHorariosDisponiveis", diasComHorariosDisponiveis);

            // ALTERADO AQUI: aponta para o novo diretório
            return "solicitacoes/formulario-solicitacao";
        }

        List<ServicoLocal> servicosConfirmacao = solicitacaoFormDTO.getServicoIds().stream()
            .map(id -> solicitacaoServicoLocalRepository.findById(id).orElse(null)) 
            .filter(s -> s != null)
            .collect(Collectors.toList());

        model.addAttribute("solicitacaoConfirmacao", solicitacaoFormDTO); 
        model.addAttribute("servicosConfirmacao", servicosConfirmacao);

        System.out.println("Dados para confirmação: " + solicitacaoFormDTO);

        // ESTE JÁ ESTAVA CERTO, POIS JÁ ESTAVA EM "solicitacoes/confirmar-solicitacao"
        return "solicitacoes/confirmar-solicitacao";
    }

    @PostMapping("/confirmar")
    public String confirmarSolicitacao(
            @ModelAttribute("solicitacaoConfirmacao") SolicitacaoFormDTO solicitacaoFormDTO,
            RedirectAttributes redirectAttributes,
            Model model
        ) {
        System.out.println("Confirmando e enviando solicitação: " + solicitacaoFormDTO);

        try {
            solicitacaoService.criarSolicitacao(solicitacaoFormDTO);

            redirectAttributes.addFlashAttribute("mensagemSucesso", "Solicitação enviada e confirmada com sucesso!");
            // ESTE REDIRECIONA PARA UM GET, A URL JÁ É O SUFICIENTE
            return "redirect:/solicitacoes/nova";
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro de validação ou estado ao confirmar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
            // ESTE REDIRECIONA PARA UM GET, A URL JÁ É O SUFICIENTE
            return "redirect:/solicitacoes/nova"; 
        } catch (Exception e) {
            System.err.println("Erro inesperado ao confirmar solicitação: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensagemErro", "Ocorreu um erro inesperado ao confirmar sua solicitação. Tente novamente.");
            // ESTE REDIRECIONA PARA UM GET, A URL JÁ É O SUFICIENTE
            return "redirect:/solicitacoes/nova"; 
        }
    }


    @GetMapping("/listar")
    public String listarSolicitacoes(Model model) {
        List<Solicitacao> solicitacoes = solicitacaoService.listarSolicitacoesMaisAtuais();
        model.addAttribute("solicitacoes", solicitacoes);
        // ALTERADO AQUI: aponta para o novo diretório
        return "solicitacoes/listar-solicitacoes";
    }
}