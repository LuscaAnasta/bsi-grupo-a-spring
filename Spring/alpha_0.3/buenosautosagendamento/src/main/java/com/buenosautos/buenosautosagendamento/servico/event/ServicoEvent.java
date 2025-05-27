package com.buenosautos.buenosautosagendamento.servico.event;

import java.math.BigDecimal;
import java.io.Serializable;

public class ServicoEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String codigo;
    private String nome;
    private BigDecimal preco;
    private String type; // "CREATED", "UPDATED", "DELETED"

    public ServicoEvent() {}

    public ServicoEvent(Long id, String codigo, String nome, BigDecimal preco, String type) {
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.preco = preco;
        this.type = type;
    }
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}