package com.buenosautos.buenosautosagendamento.modelos;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;


public abstract class ServicoModelo {

    private String codigo;
    private String nome;
    private BigDecimal preco;


    // Construtor vazio (obrigatório para JPA)
    public ServicoModelo() {
    }

    // Construtor com argumentos
    public ServicoModelo(String codigo, String nome, BigDecimal preco) {
        this.codigo = codigo;
        this.nome = nome;
        this.preco = preco;
    }


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