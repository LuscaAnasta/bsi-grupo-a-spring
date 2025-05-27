package com.buenosautos.buenosautosagendamento.disponibilidade.controller;

import com.buenosautos.buenosautosagendamento.disponibilidade.model.Disponibilidade;
import com.buenosautos.buenosautosagendamento.disponibilidade.repository.DisponibilidadeRepository; // Import do Repository
import com.buenosautos.buenosautosagendamento.disponibilidade.service.DisponibilidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequestMapping("/disponibilidade")
public class DisponibilidadeController {

    private final DisponibilidadeService disponibilidadeService;
    private final DisponibilidadeRepository disponibilidadeRepository; // Repositório injetado

    @Autowired
    public DisponibilidadeController(DisponibilidadeService disponibilidadeService,
                                     DisponibilidadeRepository disponibilidadeRepository) { // Injeção via construtor
        this.disponibilidadeService = disponibilidadeService;
        this.disponibilidadeRepository = disponibilidadeRepository;
    }

    // DTO auxiliar para o formulário de definição de disponibilidade
    // Recomendado: criar esta classe em um pacote 'dto' separado (ex: com.buenosautosagendamento.disponibilidade.dto)
    public static class DisponibilidadeFormDTO {
        private LocalDate dia;
        private String horarioInicio;
        private String horarioFim;
        private int capacidadeMaximaDia; // <-- MUDAR AQUI: de capacidadeMaxima para capacidadeMaximaDia

        // Getters e Setters
        public LocalDate getDia() { return dia; }
        public void setDia(LocalDate dia) { this.dia = dia; }
        public String getHorarioInicio() { return horarioInicio; }
        public void setHorarioInicio(String horarioInicio) { this.horarioInicio = horarioInicio; }
        public String getHorarioFim() { return horarioFim; }
        public void setHorarioFim(String horarioFim) { this.horarioFim = horarioFim; }
        public int getCapacidadeMaximaDia() { return capacidadeMaximaDia; } // <-- MUDAR AQUI
        public void setCapacidadeMaximaDia(int capacidadeMaximaDia) { this.capacidadeMaximaDia = capacidadeMaximaDia; } // <-- MUDAR AQUI
    }


    /**
     * Exibe a página de gerenciamento de disponibilidade com a lista dos próximos 15 dias.
     */
    @GetMapping
    public String gerenciarDisponibilidade(Model model) {
        // A linha abaixo busca os dias que ESTÃO agendáveis (ABERTO e com vagas), útil para seleção de clientes
        // List<Disponibilidade> diasDisponiveis = disponibilidadeService.listarDiasAgendaveis();
        
        LocalDate hoje = LocalDate.now();
        // Os próximos 15 dias, incluindo o dia atual.
        // Se 'hoje' é dia 1, 'hoje.plusDays(14)' é dia 15. Total de 15 dias.
        LocalDate quinzeDiasAFrente = hoje.plusDays(14); 
        
        // Esta é a lista completa dos dias configurados (ABERTO, FECHADO, CHEIO, etc.)
        // Usamos o repositório diretamente para buscar a lista completa para exibição na tabela.
        List<Disponibilidade> todosDiasConfigurados = disponibilidadeRepository.findByDiaBetween(hoje, quinzeDiasAFrente);

        // Adicionando ao Model para que o HTML possa acessar esta lista
        model.addAttribute("todosDiasConfigurados", todosDiasConfigurados); 

        model.addAttribute("formDTO", new DisponibilidadeFormDTO()); // Para o formulário de nova configuração
        model.addAttribute("hoje", hoje); // Para controle no HTML (min/max da data)
        model.addAttribute("quinzeDiasAFrente", quinzeDiasAFrente); // Para controle no HTML (min/max da data)

        return "disponibilidade/gerenciar-disponibilidade"; // Nome do seu template HTML
    }

    /**
     * Processa a submissão do formulário para definir/atualizar a disponibilidade de um dia.
     */
    @PostMapping("/definir")
    public String definirDisponibilidade(@ModelAttribute("formDTO") DisponibilidadeFormDTO formDTO,
                                         RedirectAttributes redirectAttributes) {
        try {
            // Converte os horários de String para LocalTime
            LocalTime inicio = LocalTime.parse(formDTO.getHorarioInicio());
            LocalTime fim = LocalTime.parse(formDTO.getHorarioFim());

            // Chama o serviço para definir ou atualizar a disponibilidade do dia
            disponibilidadeService.definirDisponibilidadeDiaria(
                formDTO.getDia(), inicio, fim, formDTO.getCapacidadeMaximaDia()
            );
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Disponibilidade para " + formDTO.getDia() + " definida/atualizada com sucesso!");
        } catch (DateTimeParseException e) {
            // Erro se o formato da hora (HH:MM) estiver incorreto
            redirectAttributes.addFlashAttribute("mensagemErro", "Formato de hora inválido. Use HH:MM.");
        } catch (IllegalArgumentException e) {
            // Captura exceções lançadas pelo serviço (ex: data passada, horário inválido, capacidade menor que agendamentos)
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao definir disponibilidade: " + e.getMessage());
        } catch (Exception e) {
            // Captura qualquer outra exceção inesperada
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro inesperado ao definir disponibilidade: " + e.getMessage());
            e.printStackTrace(); // Imprime o stack trace para depuração no console do servidor
        }
        return "redirect:/disponibilidade"; // Redireciona de volta para a página de gerenciamento
    }

    /**
     * Altera o status (ABERTO/FECHADO) de um dia.
     */
    @PostMapping("/alterar-status")
    public String alterarStatus(@RequestParam("dia") String diaStr, // Data vem como String do formulário
                                @RequestParam("novoStatus") Disponibilidade.StatusDisponibilidadeDiaria novoStatus, // Enum é parseado automaticamente
                                RedirectAttributes redirectAttributes) {
        try {
            LocalDate dia = LocalDate.parse(diaStr); // Converte a String da data para LocalDate
            disponibilidadeService.alterarStatusDisponibilidade(dia, novoStatus);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Status do dia " + dia + " alterado para " + novoStatus + ".");
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Formato de data inválido. Use AAAA-MM-DD.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao alterar status: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro inesperado ao alterar status: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/disponibilidade";
    }

    // Opcional: Endpoint para remover um dia de disponibilidade
    // @PostMapping("/remover")
    // public String removerDisponibilidade(@RequestParam("dia") String diaStr, RedirectAttributes redirectAttributes) {
    //     try {
    //         LocalDate dia = LocalDate.parse(diaStr);
    //         // Você precisaria adicionar um método `removerDisponibilidade(LocalDate dia)` no seu service:
    //         // - Este método deve garantir que não há agendamentos associados ao dia antes de remover.
    //         // - Ou, se houver agendamentos, o que acontece com eles (cancelar, re-agendar, etc.)?
    //         // disponibilidadeService.removerDisponibilidade(dia);
    //         redirectAttributes.addFlashAttribute("mensagemSucesso", "Disponibilidade para " + dia + " removida.");
    //     } catch (Exception e) {
    //         redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao remover disponibilidade: " + e.getMessage());
    //     }
    //     return "redirect:/disponibilidade";
    // }
}