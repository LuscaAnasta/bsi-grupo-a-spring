package com.buenosautos.buenosautosagendamento;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.buenosautos.buenosautosagendamento.solicitacao.controller.SolicitacaoController;

@SpringBootApplication
public class BuenosautosagendamentoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuenosautosagendamentoApplication.class, args);
    }

}