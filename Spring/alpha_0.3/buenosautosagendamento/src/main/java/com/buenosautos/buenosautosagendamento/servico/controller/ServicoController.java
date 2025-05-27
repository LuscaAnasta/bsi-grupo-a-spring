package com.buenosautos.buenosautosagendamento.servico.controller;

import com.buenosautos.buenosautosagendamento.servico.model.Servico;
import com.buenosautos.buenosautosagendamento.servico.service.ServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/servicos")
public class ServicoController {

    @Autowired
    private ServicoService servicoService;

    @GetMapping("/gerenciar")
    public String listarServicos(Model model) {
        List<Servico> servicos = servicoService.listarTodosServicos();
        model.addAttribute("servicos", servicos);
        return "servicos/gerenciar-servicos"; // Ajustado o caminho do template
    }

    @GetMapping("/editar/{id}")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        Servico servico = servicoService.buscarServicoPorId(id)
                                        .orElseThrow(() -> new RuntimeException("Serviço não encontrado para edição!"));
        model.addAttribute("servico", servico);
        return "servicos/form-servico"; // Ajustado o caminho do template
    }

    @PostMapping("/salvar")
    public String salvarServico(Servico servico) {
        // A depuração que adicionei antes ajudaria a verificar o ID aqui
        if (servico.getId() == null) {
            servicoService.criarServico(servico);
        } else {
            servicoService.atualizarServico(servico.getId(), servico);
        }
        return "redirect:/servicos/gerenciar";
    }

    @PostMapping("/remover/{id}")
    public String removerServico(@PathVariable Long id) {
        servicoService.deletarServico(id);
        return "redirect:/servicos/gerenciar";
    }

    @GetMapping("/novo")
    public String exibirFormularioNovoServico(Model model) {
        model.addAttribute("servico", new Servico());
        return "servicos/form-servico"; // Ajustado o caminho do template
    }
}