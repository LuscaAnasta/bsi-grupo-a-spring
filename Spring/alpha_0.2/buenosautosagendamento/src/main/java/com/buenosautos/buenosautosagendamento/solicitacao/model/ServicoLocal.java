package com.buenosautos.buenosautosagendamento.solicitacao.model;

import java.math.BigDecimal;

public class ServicoLocal {
    private String codigo;
    private String nome;
    private BigDecimal preco;

    public ServicoLocal() {}

    public ServicoLocal(String codigo, String nome, BigDecimal preco) {
        this.codigo = codigo;
        this.nome = nome;
        this.preco = preco;
    }

    // Getters e setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }
}
