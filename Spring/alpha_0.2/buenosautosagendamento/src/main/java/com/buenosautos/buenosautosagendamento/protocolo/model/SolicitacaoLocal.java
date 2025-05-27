package com.buenosautos.buenosautosagendamento.protocolo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Embeddable;


@Embeddable
public class SolicitacaoLocal {

	private Long idOriginal;
	
	private String marca;
    private String ano;
    private String modelo;
    
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    
    private LocalDate dataSolicitacao;
    private LocalDateTime dataAgendada;
    
    public SolicitacaoLocal() {
    	dataSolicitacao = LocalDate.now();
    }

    
	public SolicitacaoLocal(Long idOriginal, String marca, String ano, String modelo, String nome, String cpf,
			String email, String telefone, LocalDateTime dataAgendada) {
		this();
		this.idOriginal = idOriginal;
		this.marca = marca;
		this.ano = ano;
		this.modelo = modelo;
		this.nome = nome;
		this.cpf = cpf;
		this.email = email;
		this.telefone = telefone;
		this.dataAgendada = dataAgendada;
	}


	public Long getIdOriginal() {
		return idOriginal;
	}

	public void setIdOriginal(Long idOriginal) {
		this.idOriginal = idOriginal;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public LocalDate getDataSolicitacao() {
		return dataSolicitacao;
	}

	public LocalDateTime getDataAgendada() {
		return dataAgendada;
	}

	public void setDataAgendada(LocalDateTime dataAgendada) {
		this.dataAgendada = dataAgendada;
	}
}
