package com.buenosautos.buenosautosagendamento.protocolo.model;

import java.math.BigDecimal;

import com.buenosautos.buenosautosagendamento.modelos.ServicoModelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ServicoMecanico extends ServicoModelo {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;

    public ServicoMecanico() {}

 

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}