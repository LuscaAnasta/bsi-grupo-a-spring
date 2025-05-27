package com.buenosautos.buenosautosagendamento.solicitacao.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;

import java.time.LocalDate; // NOVO IMPORT
import java.time.LocalTime; // NOVO IMPORT
import java.util.ArrayList;
import java.util.List;

public class SolicitacaoFormDTO {

    // Dados do Cliente
    @NotBlank(message = "O nome do cliente é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String clienteNome;

    @NotBlank(message = "O CPF do cliente é obrigatório")
    @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}", message = "CPF inválido. Use o formato XXX.XXX.XXX-XX")
    private String clienteCpf;

    @NotBlank(message = "O email do cliente é obrigatório")
    @Email(message = "Email inválido")
    private String clienteEmail;

    @NotBlank(message = "O telefone do cliente é obrigatório")
    @Pattern(regexp = "^\\(\\d{2}\\)\\s?\\d{4,5}-?\\d{4}$", message = "Telefone inválido. Use o formato (XX) XXXXX-XXXX ou (XX) XXXX-XXXX")
    private String clienteTelefone;

    // Dados do Veículo
    @NotBlank(message = "A marca do veículo é obrigatória")
    private String veiculoMarca;

    @NotBlank(message = "O modelo do veículo é obrigatório")
    private String veiculoModelo;

    @NotBlank(message = "O ano do veículo é obrigatório")
    @Pattern(regexp = "\\d{4}", message = "Ano inválido. Use o formato AAAA (ex: 2023)")
    private String veiculoAno;
    
    @NotBlank(message = "A placa do veículo é obrigatória")
    private String veiculoPlaca;

    // IDs dos serviços selecionados
    @NotNull(message = "Selecione pelo menos um serviço")
    @Size(min = 1, message = "Selecione pelo menos um serviço")
    private List<Long> servicoIds = new ArrayList<>();

    // Dados do Agendamento (AGORA SEPARADOS)
    @NotNull(message = "A data do agendamento é obrigatória")
    @FutureOrPresent(message = "A data do agendamento deve ser no presente ou futuro")
    private LocalDate dataAgendadaDia; // Campo para a data

    @NotNull(message = "O horário do agendamento é obrigatório")
    private LocalTime horarioAgendado; // Campo para o horário

    public SolicitacaoFormDTO() {}

    // Construtor completo (ATUALIZADO)
    public SolicitacaoFormDTO(String clienteNome, String clienteCpf, String clienteEmail, String clienteTelefone,
                              String veiculoMarca, String veiculoModelo, String veiculoAno, String veiculoPlaca,
                              List<Long> servicoIds, LocalDate dataAgendadaDia, LocalTime horarioAgendado) {
        this.clienteNome = clienteNome;
        this.clienteCpf = clienteCpf;
        this.clienteEmail = clienteEmail;
        this.clienteTelefone = clienteTelefone;
        this.veiculoMarca = veiculoMarca;
        this.veiculoModelo = veiculoModelo;
        this.veiculoAno = veiculoAno;
        this.veiculoPlaca = veiculoPlaca;
        this.servicoIds = (servicoIds != null) ? new ArrayList<>(servicoIds) : new ArrayList<>(); 
        this.dataAgendadaDia = dataAgendadaDia; // ATUALIZADO AQUI
        this.horarioAgendado = horarioAgendado; // NOVO CAMPO
    }

    // --- Getters e Setters ---
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
    public String getClienteCpf() { return clienteCpf; }
    public void setClienteCpf(String clienteCpf) { this.clienteCpf = clienteCpf; }
    public String getClienteEmail() { return clienteEmail; }
    public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }
    public String getClienteTelefone() { return clienteTelefone; }
    public void setClienteTelefone(String clienteTelefone) { this.clienteTelefone = clienteTelefone; }
    public String getVeiculoMarca() { return veiculoMarca; }
    public void setVeiculoMarca(String veiculoMarca) { this.veiculoMarca = veiculoMarca; }
    public String getVeiculoModelo() { return veiculoModelo; }
    public void setVeiculoModelo(String veiculoModelo) { this.veiculoModelo = veiculoModelo; }
    public String getVeiculoAno() { return veiculoAno; }
    public void setVeiculoAno(String veiculoAno) { this.veiculoAno = veiculoAno; }
    public String getVeiculoPlaca() { return veiculoPlaca; }
    public void setVeiculoPlaca(String veiculoPlaca) { this.veiculoPlaca = veiculoPlaca; }
    public List<Long> getServicoIds() { return servicoIds; }
    public void setServicoIds(List<Long> servicoIds) { this.servicoIds = servicoIds; }

    public LocalDate getDataAgendadaDia() { return dataAgendadaDia; }
    public void setDataAgendadaDia(LocalDate dataAgendadaDia) { this.dataAgendadaDia = dataAgendadaDia; }

    public LocalTime getHorarioAgendado() { return horarioAgendado; }
    public void setHorarioAgendado(LocalTime horarioAgendado) { this.horarioAgendado = horarioAgendado; }

    @Override
    public String toString() {
        return "SolicitacaoFormDTO{" +
               "clienteNome='" + clienteNome + '\'' +
               ", clienteCpf='" + clienteCpf + '\'' +
               ", clienteEmail='" + clienteEmail + '\'' +
               ", clienteTelefone='" + clienteTelefone + '\'' +
               ", veiculoMarca='" + veiculoMarca + '\'' +
               ", veiculoModelo='" + veiculoModelo + '\'' +
               ", veiculoAno='" + veiculoAno + '\'' +
               ", veiculoPlaca='" + veiculoPlaca + '\'' +
               ", servicoIds=" + servicoIds +
               ", dataAgendadaDia=" + dataAgendadaDia + // ATUALIZADO AQUI
               ", horarioAgendado=" + horarioAgendado + // NOVO CAMPO
               '}';
    }
}