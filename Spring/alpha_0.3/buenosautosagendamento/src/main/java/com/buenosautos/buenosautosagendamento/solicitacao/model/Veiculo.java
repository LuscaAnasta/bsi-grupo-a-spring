package com.buenosautos.buenosautosagendamento.solicitacao.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String marca;
    private String ano;
    private String modelo;
    private String placa; // Novo atributo adicionado

    // Construtor vazio (obrigatório para JPA)
    public Veiculo() {
    }

    // Construtor com argumentos
    public Veiculo(String marca, String ano, String modelo, String placa) { // Atualizado o construtor
        this.marca = marca;
        this.ano = ano;
        this.modelo = modelo;
        this.placa = placa; // Inicializa o novo atributo
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() { // Novo getter
        return placa;
    }

    public void setPlaca(String placa) { // Novo setter
        this.placa = placa;
    }
}