package com.buenosautos.buenosautosagendamento.protocolo.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList; // Importe para inicializar a lista
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PreUpdate;

@Entity
public class Protocolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private SolicitacaoLocal solicitacao;

    @OneToMany(mappedBy = "protocolo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProtocoloServicoItem> servicosProtocolo;

    @Enumerated(EnumType.STRING)
    private Status statusProtocolo;

    private LocalDate dataProtocolo;
    private LocalDate dataAgendadaProtocolo;
    
    @Column(unique = true)
    private String numeroProtocolo;

    public enum Status {
        AGUARDANDO_CONFIRMACAO_MECANICO,
        CONFIRMADO,
        EM_EXECUCAO,
        AGUARDANDO_PECAS,
        CONCLUIDO_AGUARDANDO_RETIDADA_CLIENTE,
        CONCLUIDO_FINALIZADO,
        CANCELADO,
        NAO_COMPARECEU
    }

    public Protocolo() {
        this.statusProtocolo = Status.AGUARDANDO_CONFIRMACAO_MECANICO;
        this.dataProtocolo = LocalDate.now();
        this.servicosProtocolo = new ArrayList<>(); // IMPORTANTE: Inicialize a lista!
    }
    
    @PostPersist
    @PreUpdate
    public void gerarNumeroProtocolo() {
        // Garante que o ID e a solicitação estejam disponíveis
        if (this.id == null || this.solicitacao == null || this.solicitacao.getDataSolicitacao() == null) {
            return; 
        }

        // Usamos a data da SOLICITACAO, como você especificou
        LocalDate dataBase = this.solicitacao.getDataSolicitacao();
        
        // Formato da data: MMYYDD (Ex: 052524 para 24 de maio de 2025)
        // DateTimeFormatter.ofPattern("MMddyy") para MM DD AA
        String dataFormatada = dataBase.format(DateTimeFormatter.ofPattern("MMddyy")); 

        // Formato do ID: XXXX (com zeros à esquerda se necessário, 4 dígitos)
        String idFormatado = String.format("%04d", this.id); 

        // Constrói o número de protocolo no formato "PMMDDYYXXXX"
        this.numeroProtocolo = "P" + dataFormatada + idFormatado; 
    }

    public Long getId() {
        return id;
    }

    public SolicitacaoLocal getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoLocal solicitacao) {
        this.solicitacao = solicitacao;
    }

    // Getter e Setter para a nova lista
    public List<ProtocoloServicoItem> getServicosProtocolo() {
        return servicosProtocolo;
    }

    public void setServicosProtocolo(List<ProtocoloServicoItem> servicosProtocolo) {
        this.servicosProtocolo = servicosProtocolo;
    }
    
    // Método auxiliar para adicionar um serviço com observação
    public void adicionarServico(Long idOriginalServico, String codigoServico, String nomeServico, BigDecimal precoServico, String observacoes) {
        ProtocoloServicoItem item = new ProtocoloServicoItem(this, idOriginalServico, codigoServico, nomeServico, precoServico, observacoes);
        this.servicosProtocolo.add(item);
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

    public LocalDate getDataAgendadaProtocolo() {
        return dataAgendadaProtocolo;
    }

    public void setDataAgendadaProtocolo(LocalDate dataAgendadaProtocolo) {
        this.dataAgendadaProtocolo = dataAgendadaProtocolo;
    }
    
    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }
}