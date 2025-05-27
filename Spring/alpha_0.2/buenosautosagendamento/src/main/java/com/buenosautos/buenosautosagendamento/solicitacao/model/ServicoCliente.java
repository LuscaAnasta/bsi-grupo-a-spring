package com.buenosautos.buenosautosagendamento.solicitacao.model;

import java.math.BigDecimal;

import com.buenosautos.buenosautosagendamento.modelos.ServicoModelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // ou SINGLE_TABLE ou TABLE_PER_CLASS
public class ServicoCliente extends ServicoModelo{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Construtor vazio (obrigatório para JPA)
    public ServicoCliente() {
    }

    // Construtor com argumentos
    public ServicoCliente(String codigo, String nome, BigDecimal preco) {
        super();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

 