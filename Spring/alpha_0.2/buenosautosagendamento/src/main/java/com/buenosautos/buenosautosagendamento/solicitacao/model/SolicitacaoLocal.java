package com.buenosautos.buenosautosagendamento.solicitacao.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.buenosautos.buenosautosagendamento.modelos.ServicoModelo;


public class SolicitacaoLocal {

	private Long idOriginal;
	
	private String marca;
    private String ano;
    private String modelo;
    
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    
    private List<ServicoLocal> servicos;

    private LocalDate dataSolicitacao;
	private LocalDateTime dataAgendada;
	
	public SolicitacaoLocal() {
		
	}

  


	public SolicitacaoLocal(Long idOriginal, String marca, String ano, String modelo, String nome, String cpf,
			String email, String telefone, List<ServicoLocal> servicos, LocalDate dataSolicitacao,
			LocalDateTime dataAgendada) {
		this.idOriginal = idOriginal;
		this.marca = marca;
		this.ano = ano;
		this.modelo = modelo;
		this.nome = nome;
		this.cpf = cpf;
		this.email = email;
		this.telefone = telefone;
		this.servicos = servicos;
		this.dataSolicitacao = dataSolicitacao;
		this.dataAgendada = dataAgendada;
	}

	
public SolicitacaoLocal toSolicitacaoLocal(Solicitacao solicitacao) {
		
		List<ServicoLocal> servicosLocais = solicitacao.getServicos().stream()
		        .map(s -> new ServicoLocal(s.getCodigo(), s.getNome(), s.getPreco()))
		        .toList();

		return new SolicitacaoLocal(
		        solicitacao.getId(),
		        solicitacao.getVeiculo().getMarca(),
		        solicitacao.getVeiculo().getAno(),
		        solicitacao.getVeiculo().getModelo(),
		        solicitacao.getCliente().getNome(),
		        solicitacao.getCliente().getCpf(),
		        solicitacao.getCliente().getEmail(),
		        solicitacao.getCliente().getTelefone(),
		        servicosLocais,
		        solicitacao.getDataSolicitacao(),
		        solicitacao.getDataAgendada()
		    );
	}








	public List<ServicoLocal> getServicos() {
		return servicos;
	}

	public void setServicos(List<ServicoLocal> servicos) {
		this.servicos = servicos;
	}




	public void setDataSolicitacao(LocalDate dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
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
