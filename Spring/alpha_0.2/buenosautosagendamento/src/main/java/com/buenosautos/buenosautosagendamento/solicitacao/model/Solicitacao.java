package com.buenosautos.buenosautosagendamento.solicitacao.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;

@Entity
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Cliente cliente;

    @ManyToMany
    private List<ServicoCliente> servicos;

    @ManyToOne(cascade = CascadeType.ALL)
    private Veiculo veiculo;

    private LocalDate dataSolicitacao;
    private LocalDateTime dataAgendada;

    // Construtor vazio
    public Solicitacao() {
        this.dataSolicitacao = LocalDate.now();
    }

    // Construtor com argumentos
    public Solicitacao(Cliente cliente, ArrayList<ServicoCliente> servicos, Veiculo veiculo, LocalDateTime dataAgendada) {
        this.cliente = cliente;
        this.servicos = servicos;
        this.veiculo = veiculo;
        this.dataSolicitacao = LocalDate.now();
        this.dataAgendada = dataAgendada;
    }

    // Getters e Setters
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

    public List<ServicoCliente> getServicos() {
        return servicos;
    }

    public void setServicos(List<ServicoCliente> servicos) {
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