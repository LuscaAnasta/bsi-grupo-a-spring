package com.buenosautos.buenosautosagendamento.protocolo.model;

import java.time.LocalDate;
import java.util.List;



import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Protocolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private SolicitacaoLocal solicitacao;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ServicoMecanico> servicosProtocolo;

    @Enumerated(EnumType.STRING)
    private Status statusProtocolo;

    private LocalDate dataProtocolo;
    private LocalDate dataAgendadaProtocolo;

    // Enum de status
    public enum Status {
        AGUARDANDO_CONFIRMACAO_MECANICO,
        CONFIRMADO,
        EM_EXECUCAO,
        AGUARDANDO_PECAS,
        CONCLUIDO,
        CANCELADO,
        NAO_COMPARECEU
    }

    // Construtor padrão
    public Protocolo() {
        this.statusProtocolo = Status.AGUARDANDO_CONFIRMACAO_MECANICO;
        this.dataProtocolo = LocalDate.now();
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoLocal getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoLocal solicitacao) {
        this.solicitacao = solicitacao;
    }

    public List<ServicoMecanico> getServicosProtocolo() {
        return servicosProtocolo;
    }

    public void setServicosProtocolo(List<ServicoMecanico> servicosProtocolo) {
        this.servicosProtocolo = servicosProtocolo;
    }

    public Status getStatusProtocolo() {
        return statusProtocolo;
    }

    public void setStatusProtocolo(Status statusProtocolo) {
        this.statusProtocolo = statusProtocolo;
    }

    public LocalDate getDataProtocolo() {
        return dataProtocolo;
    }

    public void setDataProtocolo(LocalDate dataProtocolo) {
        this.dataProtocolo = dataProtocolo;
    }

    public LocalDate getDataAgendadaProtocolo() {
        return dataAgendadaProtocolo;
    }

    public void setDataAgendadaProtocolo(LocalDate dataAgendadaProtocolo) {
        this.dataAgendadaProtocolo = dataAgendadaProtocolo;
    }
}

