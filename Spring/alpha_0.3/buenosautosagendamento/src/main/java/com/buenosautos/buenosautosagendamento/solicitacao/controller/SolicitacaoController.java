package com.buenosautos.buenosautosagendamento.solicitacao.controller;

import com.buenosautos.buenosautosagendamento.solicitacao.dto.SolicitacaoFormDTO;
import com.buenosautos.buenosautosagendamento.solicitacao.model.ServicoLocal;
import com.buenosautos.buenosautosagendamento.solicitacao.model.Solicitacao;
import com.buenosautos.buenosautosagendamento.solicitacao.repository.ServicoLocalRepository;
import com.buenosautos.buenosautosagendamento.solicitacao.service.SolicitacaoService;
import com.buenosautos.buenosautosagendamento.disponibilidade.service.DisponibilidadeService;
import com.buenosautos.buenosautosagendamento.disponibilidade.dto.DiaDisponivelDTO; // Certifique-se de que este DTO existe

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Para flash attributes (mensagens)

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
    private DisponibilidadeService disponibilidadeService; // Injetado para listar dias/horários

    @GetMapping("/nova")
    public String exibirFormularioSolicitacao(Model model) {
        model.addAttribute("solicitacaoForm", new SolicitacaoFormDTO());

        // Para popular os dropdowns de data e hora com base na disponibilidade
        List<DiaDisponivelDTO> diasComHorariosDisponiveis = disponibilidadeService.listarDiasComHorariosDisponiveisParaAgendamento();
        model.addAttribute("diasComHorariosDisponiveis", diasComHorariosDisponiveis);


        List<ServicoLocal> servicosDisponiveis = solicitacaoServicoLocalRepository.findAll().stream()
            .filter(s -> s.getStatus() == ServicoLocal.Status.ATIVO)
            .filter(s -> s.getCodigo() != null && s.getCodigo().startsWith("C")) // Exemplo de filtro
            .collect(Collectors.toList());

        if (servicosDisponiveis.isEmpty()) {
            model.addAttribute("erroServicos", "Nenhum serviço disponível que atenda aos critérios.");
        }
        model.addAttribute("servicosDisponiveis", servicosDisponiveis);

        return "formulario-solicitacao";
    }

    // ALTERADO: Este método agora exibe a tela de confirmação
    @PostMapping("/nova")
    public String processarFormularioSolicitacao(
            @Valid @ModelAttribute("solicitacaoForm") SolicitacaoFormDTO solicitacaoFormDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes // Para mensagens de erro/sucesso (não usado diretamente aqui, mas boa prática)
        ) {
        // Se houver erros de validação do DTO, retorna para o formulário inicial
        if (bindingResult.hasErrors()) {
            // Repopula os dados necessários para o formulário para exibir os erros
            List<ServicoLocal> servicosDisponiveis = solicitacaoServicoLocalRepository.findAll().stream()
                .filter(s -> s.getStatus() == ServicoLocal.Status.ATIVO)
                .filter(s -> s.getCodigo() != null && s.getCodigo().startsWith("C"))
                .collect(Collectors.toList());
            model.addAttribute("servicosDisponiveis", servicosDisponiveis);
            
            List<DiaDisponivelDTO> diasComHorariosDisponiveis = disponibilidadeService.listarDiasComHorariosDisponiveisParaAgendamento();
            model.addAttribute("diasComHorariosDisponiveis", diasComHorariosDisponiveis);

            return "formulario-solicitacao";
        }

        // Tenta fazer uma pré-validação da disponibilidade APENAS PARA EXIBIÇÃO do erro ao usuário.
        // A validação real e a reserva da vaga acontecerão no método final de confirmação transacional.
        try {
            // Esta chamada é apenas para obter a disponibilidade do dia e exibir possíveis erros de validação
            // ANTES de ir para a tela de confirmação.
            // O SolicitacaoService já tem a lógica de verificar se o dia está FECHADO/CHEIO/PASSADO.
            LocalDate dataAgendadaDia = solicitacaoFormDTO.getDataAgendadaDia();
            disponibilidadeService.consultarDisponibilidadeParaAgendamento(dataAgendadaDia)
                .orElseThrow(() -> new IllegalArgumentException("O dia selecionado não está disponível para agendamento."));

            // Validação de horário específico dentro do intervalo do dia
            // (Esta lógica já está no SolicitacaoService, mas podemos duplicar aqui para um feedback mais rápido)
            // Optional<Disponibilidade> optDisponibilidade = disponibilidadeService.consultarDisponibilidadeParaAgendamento(dataAgendadaDia);
            // if (optDisponibilidade.isPresent()) {
            //     Disponibilidade dispoDia = optDisponibilidade.get();
            //     LocalTime horarioAgendado = solicitacaoFormDTO.getHorarioAgendado();
            //     if (horarioAgendado.isBefore(dispoDia.getHorarioInicio()) || horarioAgendado.isAfter(dispoDia.getHorarioFim())) {
            //         throw new IllegalArgumentException("O horário selecionado está fora do horário de trabalho do dia.");
            //     }
            // }

        } catch (IllegalArgumentException e) {
            // Adiciona o erro ao BindingResult para que o Thymeleaf possa exibir no formulário original
            bindingResult.rejectValue("dataAgendadaDia", "disponibilidade.indisponivel", e.getMessage());
            // Repopula os dados para o formulário
            List<ServicoLocal> servicosDisponiveis = solicitacaoServicoLocalRepository.findAll().stream()
                .filter(s -> s.getStatus() == ServicoLocal.Status.ATIVO)
                .filter(s -> s.getCodigo() != null && s.getCodigo().startsWith("C"))
                .collect(Collectors.toList());
            model.addAttribute("servicosDisponiveis", servicosDisponiveis);
            
            List<DiaDisponivelDTO> diasComHorariosDisponiveis = disponibilidadeService.listarDiasComHorariosDisponiveisParaAgendamento();
            model.addAttribute("diasComHorariosDisponiveis", diasComHorariosDisponiveis);

            return "formulario-solicitacao"; // Volta para o formulário com o erro de disponibilidade
        }


        // Para a tela de confirmação, precisamos dos nomes dos serviços
        List<ServicoLocal> servicosConfirmacao = solicitacaoFormDTO.getServicoIds().stream()
            .map(id -> solicitacaoServicoLocalRepository.findById(id).orElse(null)) 
            .filter(s -> s != null)
            .collect(Collectors.toList());

        model.addAttribute("solicitacaoConfirmacao", solicitacaoFormDTO); 
        model.addAttribute("servicosConfirmacao", servicosConfirmacao);

        System.out.println("Dados para confirmação: " + solicitacaoFormDTO);

        return "solicitacoes/confirmar-solicitacao"; // Redireciona para o novo template de confirmação
    }

    // NOVO: Endpoint para o usuário confirmar o envio da solicitação
    @PostMapping("/confirmar")
    public String confirmarSolicitacao(
            @ModelAttribute("solicitacaoConfirmacao") SolicitacaoFormDTO solicitacaoFormDTO, // Recebe o DTO do formulário de confirmação
            RedirectAttributes redirectAttributes,
            Model model // Adicionado para repopular serviços se algo der errado no backend
        ) {
        System.out.println("Confirmando e enviando solicitação: " + solicitacaoFormDTO);

        try {
            // Aqui é onde o serviço é chamado para criar a solicitação e reservar a vaga
            solicitacaoService.criarSolicitacao(solicitacaoFormDTO);

            redirectAttributes.addFlashAttribute("mensagemSucesso", "Solicitação enviada e confirmada com sucesso!");
            return "redirect:/solicitacoes/nova"; // Redireciona para o formulário limpo após sucesso
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Erros esperados do DisponibilidadeService ou validações de negócio
            System.err.println("Erro de validação ou estado ao confirmar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
            // Se der erro na confirmação, o usuário é redirecionado para a tela inicial /nova
            // Poderíamos redirecionar para a confirmação de novo com uma mensagem de erro,
            // mas para simplicidade, volta para o formulário principal.
            return "redirect:/solicitacoes/nova"; 
        } catch (Exception e) {
            System.err.println("Erro inesperado ao confirmar solicitação: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensagemErro", "Ocorreu um erro inesperado ao confirmar sua solicitação. Tente novamente.");
            return "redirect:/solicitacoes/nova"; 
        }
    }


    @GetMapping("/listar")
    public String listarSolicitacoes(Model model) {
        List<Solicitacao> solicitacoes = solicitacaoService.listarSolicitacoesMaisAtuais();
        model.addAttribute("solicitacoes", solicitacoes);
        return "solicitacoes/listar-solicitacoes";
    }
}