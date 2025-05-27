package com.buenosautos.buenosautosagendamento.servico.model;

import java.math.BigDecimal;

import com.buenosautos.buenosautosagendamento.modelos.ServicoModelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Servico extends ServicoModelo{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	public Servico(String codigo, String nome, BigDecimal preco) {
		super(codigo, nome, preco);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
