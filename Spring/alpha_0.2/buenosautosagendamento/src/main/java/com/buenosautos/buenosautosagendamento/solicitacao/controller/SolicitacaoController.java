package com.buenosautos.buenosautosagendamento.solicitacao.controller;

import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.expression.Lists;

import com.buenosautos.buenosautosagendamento.modelos.ServicoModelo;
import com.buenosautos.buenosautosagendamento.solicitacao.model.Cliente;
import com.buenosautos.buenosautosagendamento.solicitacao.model.ServicoCliente;
import com.buenosautos.buenosautosagendamento.solicitacao.model.ServicoLocal;
import com.buenosautos.buenosautosagendamento.solicitacao.model.Solicitacao;
import com.buenosautos.buenosautosagendamento.solicitacao.model.SolicitacaoLocal;
import com.buenosautos.buenosautosagendamento.solicitacao.model.Veiculo;
import com.buenosautos.buenosautosagendamento.solicitacao.repository.ServicoClienteRepository;
import com.buenosautos.buenosautosagendamento.solicitacao.repository.SolicitacaoRepository;
import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Controller
public class SolicitacaoController {

	private Solicitacao solicitacao;
	
	@Autowired
	private SolicitacaoRepository solicitacaoRepo;
	
	@Autowired 
	private ServicoClienteRepository servicoRepo;
	
	@Autowired
	private RestTemplate restTemplate;
	
	
	@GetMapping("/solicitacao")
    public String mostrarFormulario(Model model) {
      
        List<ServicoCliente> servicos = servicoRepo.findAll();
        model.addAttribute("servicos", servicos);
        model.addAttribute("solicitacao", new Solicitacao());
        return "view/solicitacao/solicitacao";
    }
	
	@PostMapping("/solicitacao")
	public String processarFormulario(@ModelAttribute Solicitacao solicitacao,
	                                  @RequestParam(value = "servicoIds", required = false) List<Long> servicoIds,
	                                  @RequestParam("data") String data,
	                                  @RequestParam("hora") String hora,
	                                  RedirectAttributes redirectAttributes,
	                                  Model model) {
	    // Verificar serviços
	    if (servicoIds == null || servicoIds.isEmpty()) {
	        model.addAttribute("error", "Pelo menos um serviço deve ser selecionado.");
	        model.addAttribute("servicos", servicoRepo.findAll());
	        model.addAttribute("solicitacao", solicitacao);
	        return "view/solicitacao/solicitacao";
	    }

	    // Parse da data e hora para LocalDateTime
	    try {
	        LocalDate date = LocalDate.parse(data);
	        LocalTime time = LocalTime.parse(hora);
	        solicitacao.setDataAgendada(LocalDateTime.of(date, time));
	    } catch (DateTimeParseException e) {
	        model.addAttribute("error", "Data ou hora inválida.");
	        model.addAttribute("servicos", servicoRepo.findAll());
	        model.addAttribute("solicitacao", solicitacao);
	        return "view/solicitacao/solicitacao";
	    }

	    // Setar serviços
	    List<ServicoCliente> servicosSelecionados = servicoRepo.findAllById(servicoIds);
	    solicitacao.setServicos(servicosSelecionados);

	    solicitacaoRepo.save(solicitacao);
	    redirectAttributes.addFlashAttribute("solicitacao", solicitacao);
	    
	    return "redirect:/solicitacao/solicitacao_resposta";
	}
	
	@GetMapping("/solicitacao_resposta")
	public String confirmarSolicitacao(@ModelAttribute("solicitacao") Solicitacao solicitacao, Model model) {
	    
		SolicitacaoLocal sLocal = new SolicitacaoLocal();
		sLocal = sLocal.toSolicitacaoLocal(solicitacao);
	    model.addAttribute("solicitacao", solicitacao);
		return "view/solicitacao/solicitacao_confirmar";
	}
	
	

	
	public void addSolicitacao() {
		
		
		solicitacao = new Solicitacao();
		Cliente c = new Cliente();
		c.setNome("lucas");
		c.setCpf("123");
		c.setEmail("123");
		c.setTelefone("123");
		solicitacao.setCliente(c);
		
		Veiculo v = new Veiculo();
		v.setMarca("fiat");
		v.setModelo("carro");
		v.setAno("2009");
		solicitacao.setVeiculo(v);
		
		
		solicitacao.setServicos(retorneServico());
		
		System.out.print(solicitacao.getDataSolicitacao());
		
		solicitacaoRepo.save(solicitacao);
		
	}
	
	public List<ServicoCliente> retorneServico(){
		List<ServicoCliente> ss = new ArrayList<>();
		ss = servicoRepo.findAll();
		return ss;
	}
	
	@GetMapping("/solicitacaoteste")
	public String greeting() {
		addSolicitacao();
		return "view/solicitacao/solicitacao_teste";
	}
}
