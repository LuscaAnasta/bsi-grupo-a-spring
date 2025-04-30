package com.buenosautos.buenosautosagendamento.solicitacao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.buenosautos.buenosautosagendamento.solicitacao.model.*;
import com.buenosautos.buenosautosagendamento.solicitacao.repository.ServicoRepository;
import com.buenosautos.buenosautosagendamento.solicitacao.repository.SolicitacaoRepository;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class SolicitacaoController {

    @Autowired
    private ServicoRepository servicoRepository;
    
    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @GetMapping("/solicitacao")
    public String mostrarFormulario(Model model) {
        // Obtém todos os serviços do banco de dados
        List<Servico> servicos = servicoRepository.findAll();
        model.addAttribute("servicos", servicos);
        model.addAttribute("solicitacao", new Solicitacao());
        return "solicitacao"; // página solicitacao.html
    }

    @PostMapping("/solicitacao")
    public String processarFormulario(@Valid Solicitacao solicitacao, BindingResult result, Model model) {
        // Verificar se houve algum erro de validação
        if (result.hasErrors()) {
            model.addAttribute("servicos", servicoRepository.findAll());
            return "solicitacao"; // Se houver erro, volta para a página de solicitação
        }
        
     // Verificar se ao menos um serviço foi selecionado
        if (solicitacao.getServicos() == null || solicitacao.getServicos().isEmpty()) {
            model.addAttribute("error", "Pelo menos um serviço deve ser selecionado.");
            model.addAttribute("servicos", servicoRepository.findAll()); // Carregar todos os serviços
            return "solicitacao"; // Retorna para a página de solicitação com erro
        }

        // Verificar se todos os campos obrigatórios estão preenchidos (Cliente, Veículo e Data Agendada)
        if (solicitacao.getCliente() == null || solicitacao.getVeiculo() == null || solicitacao.getDataAgendada() == null) {
            model.addAttribute("error", "Todos os campos obrigatórios devem ser preenchidos.");
            model.addAttribute("servicos", servicoRepository.findAll()); // Carregar todos os serviços
            return "solicitacao"; // Retorna para a página de solicitação com erro
        }
        
        solicitacaoRepository.save(solicitacao);
        // Aqui você processa a solicitação, como salvar no banco de dados.
        // Adicione o código para salvar a solicitação, cliente, veiculo, etc.

        return "redirect:/solicitacao/confirmacao"; // Página de confirmação
    }
}




