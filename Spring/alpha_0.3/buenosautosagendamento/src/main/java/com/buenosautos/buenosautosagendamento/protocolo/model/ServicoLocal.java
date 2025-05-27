package com.buenosautos.buenosautosagendamento.protocolo.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "ServicoLocalProtocolo")
public class ServicoLocal {
	@Id
	private long id;
	private String codigo;
	private String nome;
	private BigDecimal preco;
	@Enumerated(EnumType.STRING)
	private Status status;
	
	public enum Status {
	    ATIVO,
	    DESATIVADO
	}
	
	public ServicoLocal() {
		this.status = status.ATIVO;
	}
	
	public ServicoLocal(Long id, String codigo, String nome, BigDecimal preco) {
		this();
		this.id = id;
		this.codigo = codigo;
		this.nome = nome;
		this.preco = preco;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public BigDecimal getPreco() {
		return preco;
	}
	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	
}