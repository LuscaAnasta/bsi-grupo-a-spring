package com.buenosautos.buenosautosagendamento.solicitacao.controller;

import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.thymeleaf.expression.Lists;

import com.buenosautos.buenosautosagendamento.solicitacao.model.Cliente;
import com.buenosautos.buenosautosagendamento.solicitacao.model.Servico;
import com.buenosautos.buenosautosagendamento.solicitacao.model.Solicitacao;
import com.buenosautos.buenosautosagendamento.solicitacao.model.Veiculo;
import com.buenosautos.buenosautosagendamento.solicitacao.repository.ServicoRepository;
import com.buenosautos.buenosautosagendamento.solicitacao.repository.SolicitacaoRepository;

@Controller
public class SolicitacaoController {

	private Solicitacao solicitacao;
	
	@Autowired
	private SolicitacaoRepository solicitacaoRepo;
	
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
		
		ArrayList<Servico> ss = new ArrayList<>();
		Servico s =  new Servico();
		s.setCodigo("C234");
		s.setNome("mecanico");
		s.setPreco(new BigDecimal(100));
		ss.add(s);
		solicitacao.setServicos(ss);
		
		solicitacao.setDataAgendada("10-10-2002");
		System.out.print(solicitacao.getDataSolicitacao());
		
		solicitacaoRepo.save(solicitacao);
		
	}
}
