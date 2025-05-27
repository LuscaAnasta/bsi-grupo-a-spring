package com.buenosautos.buenosautosagendamento.protocolo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "protocolo_servico_item") // Nome da tabela no banco de dados
public class ProtocoloServicoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID próprio para a entidade de associação

    @ManyToOne // Muitas ProtocoloServicoItem para um Protocolo
    @JoinColumn(name = "protocolo_id", nullable = false) // Coluna da chave estrangeira
    private Protocolo protocolo;

    @ManyToOne // Muitas ProtocoloServicoItem para um ServicoLocal
    @JoinColumn(name = "servico_local_id", nullable = false) // Coluna da chave estrangeira
    private ServicoLocal servicoLocal;

    private String observacoes; // O campo para suas observações

    // Construtores
    public ProtocoloServicoItem() {}

    public ProtocoloServicoItem(Protocolo protocolo, ServicoLocal servicoLocal, String observacoes) {
        this.protocolo = protocolo;
        this.servicoLocal = servicoLocal;
        this.observacoes = observacoes;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Protocolo getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(Protocolo protocolo) {
        this.protocolo = protocolo;
    }

    public ServicoLocal getServicoLocal() {
        return servicoLocal;
    }

    public void setServicoLocal(ServicoLocal servicoLocal) {
        this.servicoLocal = servicoLocal;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}