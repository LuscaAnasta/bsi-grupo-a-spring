package com.buenosautos.buenosautosagendamento.solicitacao.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;

@Entity
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Cliente cliente;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Servico> servicos;

    @ManyToOne(cascade = CascadeType.ALL)
    private Veiculo veiculo;

    private LocalDate dataSolicitacao;
    private String dataAgendada;

    // Construtor vazio
    public Solicitacao() {
        this.dataSolicitacao = LocalDate.now();
    }

    // Construtor com argumentos
    public Solicitacao(Cliente cliente, List<Servico> servicos, Veiculo veiculo, String dataAgendada) {
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

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> servicos) {
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

    public String getDataAgendada() {
        return dataAgendada;
    }

    public void setDataAgendada(String dataAgendada) {
        this.dataAgendada = dataAgendada;
    }
}