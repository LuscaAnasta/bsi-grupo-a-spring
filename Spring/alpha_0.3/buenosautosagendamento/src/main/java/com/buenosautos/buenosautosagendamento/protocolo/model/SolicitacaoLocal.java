package com.buenosautos.buenosautosagendamento.protocolo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id; // Adicione o @Id
import jakarta.persistence.GeneratedValue; // Remova este ou use uma estratégia customizada

@Entity
public class SolicitacaoLocal {

    @Id // Essencial para ser uma entidade JPA
    // Não use @GeneratedValue(strategy = GenerationType.IDENTITY) aqui!
    // O ID deve vir do microsserviço de origem.
    private Long id; // Nome claro para indicar que é o ID do serviço original
    
    // Atributos de Veiculo "achatados"
    private String marca;
    private String ano;
    private String modelo;
    private String placa;
    
    // Atributos de Cliente "achatados"
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    
    // Atributos da Solicitacao original
    private LocalDate dataSolicitacao;
    private LocalDateTime dataAgendada;
    
    // Adicione o campo de status (muito recomendado)
    @Enumerated(EnumType.STRING)
    private Status statusLocal; // Enum ATIVO/DESATIVADO, como discutimos para ServicoLocal

    public enum Status{
    	ATIVO,
    	DESATIVADO
    }
    
    public SolicitacaoLocal() {
        this.statusLocal = Status.ATIVO; // Padrão
    }
    
    public SolicitacaoLocal(Long id, String nome, String cpf, String email,
            String telefone, String marca, String modelo, String ano,
            String placa, // <<-- NOVO PARÂMETRO AQUI
            LocalDate dataSolicitacao, LocalDateTime dataAgendada) {
			this(); // Chama o construtor padrão para inicializar statusLocal
			this.id = id;
			this.nome = nome;
			this.cpf = cpf;
			this.email = email;
			this.telefone = telefone;
			
			this.marca = marca;
			this.ano = ano;
			this.modelo = modelo;
			this.placa = placa; // <<-- INICIALIZAÇÃO AQUI
			
			this.dataSolicitacao = dataSolicitacao;
			this.dataAgendada = dataAgendada;
			}


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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public LocalDate getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(LocalDate dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public LocalDateTime getDataAgendada() {
		return dataAgendada;
	}

	public void setDataAgendada(LocalDateTime dataAgendada) {
		this.dataAgendada = dataAgendada;
	}

	public Status getStatusLocal() {
		return statusLocal;
	}

	public void setStatusLocal(Status statusLocal) {
		this.statusLocal = statusLocal;
	}
	
	public String getPlaca() { // NOVO GETTER
        return placa;
    }

    public void setPlaca(String placa) { // NOVO SETTER
        this.placa = placa;
    }

    
}