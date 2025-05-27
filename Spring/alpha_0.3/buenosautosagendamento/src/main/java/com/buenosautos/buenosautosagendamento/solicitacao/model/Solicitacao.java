package com.buenosautos.buenosautosagendamento.solicitacao.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cliente cliente;

    @ManyToMany
    private List<ServicoLocal> servicos;

    @ManyToOne
    private Veiculo veiculo;

    private LocalDate dataSolicitacao;
    private LocalDateTime dataAgendada;

    // Construtor vazio
    public Solicitacao() {
        this.dataSolicitacao = LocalDate.now();
    }

	public Solicitacao(Cliente cliente, List<ServicoLocal> servicos, Veiculo veiculo, LocalDateTime dataAgendada) {
		this.cliente = cliente;
		this.servicos = servicos;
		this.veiculo = veiculo;
		this.dataAgendada = dataAgendada;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<ServicoLocal> getServicos() {
		return servicos;
	}

	public void setServicos(List<ServicoLocal> servicos) {
		this.servicos = servicos;
	}

	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	public LocalDate getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(LocalDate dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public LocalDateTime getDataAgendada() {
		return dataAgendada;
	}

	public void setDataAgendada(LocalDateTime dataAgendada) {
		this.dataAgendada = dataAgendada;
	}
	
	
    

}