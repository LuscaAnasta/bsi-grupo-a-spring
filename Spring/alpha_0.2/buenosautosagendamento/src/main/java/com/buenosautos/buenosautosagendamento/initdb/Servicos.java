package com.buenosautos.buenosautosagendamento.initdb;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.buenosautos.buenosautosagendamento.servico.model.Servico;
import com.buenosautos.buenosautosagendamento.servico.repository.ServicoRepository;

@Component
public class Servicos implements CommandLineRunner {

    @Autowired
    private ServicoRepository servicoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verifique se os serviços padrão já existem
        if (servicoRepository.count() == 0) {
            // Inserir serviços padrão
        	Servico servico1 = new Servico("001", "Troca de Óleo", new BigDecimal("80.00"));
        	Servico servico2 = new Servico("002", "Alinhamento de Direção", new BigDecimal("120.00"));
        	Servico servico3 = new Servico("003", "Troca de Pneu", new BigDecimal("150.00"));
        	Servico servico4 = new Servico("004", "Balanceamento de Pneus", new BigDecimal("90.00"));
        	Servico servico5 = new Servico("005", "Troca de Pastilhas de Freio", new BigDecimal("200.00"));
        	Servico servico6 = new Servico("006", "Troca de Correia Dentada", new BigDecimal("350.00"));
        	Servico servico7 = new Servico("007", "Troca de Velas e Cabos de Ignição", new BigDecimal("120.00"));
        	Servico servico8 = new Servico("008", "Reparo no Sistema Elétrico", new BigDecimal("250.00"));
        	Servico servico9 = new Servico("009", "Instalação de Sistema de Som Automotivo", new BigDecimal("350.00"));
        	Servico servico10 = new Servico("010", "Manutenção do Sistema de Arrefecimento", new BigDecimal("180.00"));
        	Servico servico11 = new Servico("011", "Troca de Bateria", new BigDecimal("220.00"));
        	Servico servico12 = new Servico("012", "Troca de Amortecedores", new BigDecimal("300.00"));
        	Servico servico13 = new Servico("013", "Revisão Geral", new BigDecimal("250.00"));  // Novo serviço


            // Salvar os serviços no banco
            servicoRepository.save(servico1);
            servicoRepository.save(servico2);
            servicoRepository.save(servico3);
            servicoRepository.save(servico4);
            servicoRepository.save(servico5);
            servicoRepository.save(servico6);
            servicoRepository.save(servico7);
            servicoRepository.save(servico8);
            servicoRepository.save(servico9);
            servicoRepository.save(servico10);
            servicoRepository.save(servico11);
            servicoRepository.save(servico12);
            servicoRepository.save(servico13);

            System.out.println("Serviços padrão inseridos com sucesso!");
        }
    }
}