package com.buenosautos.buenosautosagendamento.initdb;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.buenosautos.buenosautosagendamento.servico.model.Servico;
import com.buenosautos.buenosautosagendamento.servico.service.ServicoService;

@Component
public class AddServicosBase implements CommandLineRunner {

    @Autowired
    private ServicoService servicoService;

    @Override
    public void run(String... args) throws Exception {
        // Verifique se os serviços padrão já existem
        if (servicoService.listarTodosServicos().isEmpty()) {
            // Inserir serviços padrão
        	servicoService.criarServico(new Servico("C001", "Troca de Óleo", new BigDecimal("80.00")));
            servicoService.criarServico(new Servico("C002", "Alinhamento de Direção", new BigDecimal("120.00")));
            servicoService.criarServico(new Servico("C003", "Troca de Pneu", new BigDecimal("150.00")));
            servicoService.criarServico(new Servico("C004", "Balanceamento de Pneus", new BigDecimal("90.00")));
            servicoService.criarServico(new Servico("C005", "Troca de Pastilhas de Freio", new BigDecimal("200.00")));
            servicoService.criarServico(new Servico("C006", "Troca de Correia Dentada", new BigDecimal("350.00")));
            servicoService.criarServico(new Servico("C007", "Troca de Velas e Cabos de Ignição", new BigDecimal("120.00")));
            servicoService.criarServico(new Servico("C008", "Reparo no Sistema Elétrico", new BigDecimal("250.00")));
            servicoService.criarServico(new Servico("C009", "Instalação de Sistema de Som Automotivo", new BigDecimal("350.00")));
            servicoService.criarServico(new Servico("C010", "Manutenção do Sistema de Arrefecimento", new BigDecimal("180.00")));
            servicoService.criarServico(new Servico("C011", "Troca de Bateria", new BigDecimal("220.00")));
            servicoService.criarServico(new Servico("C012", "Troca de Amortecedores", new BigDecimal("300.00")));
            servicoService.criarServico(new Servico("C013", "Revisão Geral", new BigDecimal("250.00")));
            servicoService.criarServico(new Servico("X001", "Serviço Fora do Padrão C", new BigDecimal("10.00"))); // Exemplo de serviço que NÃO será copiado para ServicoLocal

            System.out.println("Serviços padrão inseridos e eventos publicados!");
        } else {
            System.out.println("Serviços já existentes, pulando inserção inicial.");
        }
    }
}