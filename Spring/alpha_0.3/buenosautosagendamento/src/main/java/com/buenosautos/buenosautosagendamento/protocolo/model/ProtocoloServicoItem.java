package com.buenosautos.buenosautosagendamento.protocolo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal; // Import para BigDecimal

@Entity
@Table(name = "protocolo_servico_item")
public class ProtocoloServicoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "protocolo_id", nullable = false)
    private Protocolo protocolo;

    // Campos para o snapshot do serviço
    private Long idOriginalServico; // Opcional: ID do serviço original no módulo principal
    @Column(nullable = false)
    private String codigoServico;   // Cópia do código do serviço
    @Column(nullable = false)
    private String nomeServico;     // Cópia do nome do serviço
    @Column(nullable = false)
    private BigDecimal precoServico; // Cópia do preço do serviço

    private String observacoes;

    // Construtores
    public ProtocoloServicoItem() {}

    // Construtor atualizado para receber os detalhes do serviço
    public ProtocoloServicoItem(Protocolo protocolo, Long idOriginalServico, String codigoServico, String nomeServico, BigDecimal precoServico, String observacoes) {
        this.protocolo = protocolo;
        this.idOriginalServico = idOriginalServico;
        this.codigoServico = codigoServico;
        this.nomeServico = nomeServico;
        this.precoServico = precoServico;
        this.observacoes = observacoes;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Protocolo getProtocolo() { return protocolo; }
    public void setProtocolo(Protocolo protocolo) { this.protocolo = protocolo; }
    
    // Getters e Setters para os campos do snapshot
    public Long getIdOriginalServico() { return idOriginalServico; }
    public void setIdOriginalServico(Long idOriginalServico) { this.idOriginalServico = idOriginalServico; }
    public String getCodigoServico() { return codigoServico; }
    public void setCodigoServico(String codigoServico) { this.codigoServico = codigoServico; }
    public String getNomeServico() { return nomeServico; }
    public void setNomeServico(String nomeServico) { this.nomeServico = nomeServico; }
    public BigDecimal getPrecoServico() { return precoServico; }
    public void setPrecoServico(BigDecimal precoServico) { this.precoServico = precoServico; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}