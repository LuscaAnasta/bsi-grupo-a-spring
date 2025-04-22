package com.buenosautos.buenosautosagendamento.model.agendamento;

import java.time.LocalDate;

public class Solicitacao {

	private Cliente cliente;
	private Servico servico;
	private Veiculo veiculo;
	private LocalDate data_solicitacao;
	private String data_agendada;
	
	public Solicitacao() {
		this.data_solicitacao = LocalDate.now();
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}

	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	public String getData_agendada() {
		return data_agendada;
	}

	public void setData_agendada(String data_agendada) {
		this.data_agendada = data_agendada;
	}
	
	
}
