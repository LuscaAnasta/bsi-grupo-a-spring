package com.buenosautos.buenosautosagendamento.solicitacao.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class SolicitacaoEvent {

    private Long id;
    private String clienteNome;
    private String clienteCpf;
    private String clienteEmail;
    private String clienteTelefone;
    private String veiculoMarca;
    private String veiculoModelo;
    private String veiculoAno;
    private String veiculoPlaca; // Atributo para a placa do veículo
    private List<Long> servicoIds; // IDs dos ServicoLocal da Solicitacao original
    private LocalDate dataSolicitacao;
    private LocalDateTime dataAgendada;
    private String type; // "CREATED", "UPDATED", "DELETED"

    // Construtor principal (AGORA CORRETO)
    public SolicitacaoEvent(Long id, String clienteNome, String clienteCpf, String clienteEmail, String clienteTelefone,
                            String veiculoMarca, String veiculoModelo, String veiculoAno, String veiculoPlaca, // <<-- NOVO PARÂMETRO AQUI
                            List<Long> servicoIds, LocalDate dataSolicitacao, LocalDateTime dataAgendada, String type) {
        this.id = id;
        this.clienteNome = clienteNome;
        this.clienteCpf = clienteCpf;
        this.clienteEmail = clienteEmail;
        this.clienteTelefone = clienteTelefone;
        this.veiculoMarca = veiculoMarca;
        this.veiculoModelo = veiculoModelo;
        this.veiculoAno = veiculoAno;
        this.veiculoPlaca = veiculoPlaca; // <<-- INICIALIZAÇÃO AQUI
        this.servicoIds = servicoIds != null ? new ArrayList<>(servicoIds) : new ArrayList<>();
        this.dataSolicitacao = dataSolicitacao;
        this.dataAgendada = dataAgendada;
        this.type = type;
    }

    // Construtor para eventos de deleção (se a solicitação for logicamente "deletada")
    public SolicitacaoEvent(Long id, String type) {
        this.id = id;
        this.type = type;
        this.servicoIds = new ArrayList<>(); // Inicializa para evitar null
    }

    // Getters
    public Long getId() { return id; }
    public String getClienteNome() { return clienteNome; }
    public String getClienteCpf() { return clienteCpf; }
    public String getClienteEmail() { return clienteEmail; }
    public String getClienteTelefone() { return clienteTelefone; }
    public String getVeiculoMarca() { return veiculoMarca; }
    public String getVeiculoModelo() { return veiculoModelo; }
    public String getVeiculoAno() { return veiculoAno; }
    public String getVeiculoPlaca() { return veiculoPlaca; } // Getter para a placa
    public List<Long> getServicoIds() { return servicoIds; }
    public LocalDate getDataSolicitacao() { return dataSolicitacao; }
    public LocalDateTime getDataAgendada() { return dataAgendada; }
    public String getType() { return type; }

    // Setters (se precisar, embora para DTOs de evento, geralmente são imutáveis)
    // public void setId(Long id) { this.id = id; }
    // ...

    @Override
    public String toString() {
        return "SolicitacaoEvent{" +
               "id=" + id +
               ", clienteNome='" + clienteNome + '\'' +
               ", clienteCpf='" + clienteCpf + '\'' +
               ", clienteEmail='" + clienteEmail + '\'' +
               ", clienteTelefone='" + clienteTelefone + '\'' +
               ", veiculoMarca='" + veiculoMarca + '\'' +
               ", veiculoModelo='" + veiculoModelo + '\'' +
               ", veiculoAno='" + veiculoAno + '\'' +
               ", veiculoPlaca='" + veiculoPlaca + '\'' + // <<-- ADICIONADO AO toString
               ", servicoIds=" + servicoIds +
               ", dataSolicitacao=" + dataSolicitacao +
               ", dataAgendada=" + dataAgendada +
               ", type='" + type + '\'' +
               '}';
    }
}