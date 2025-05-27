package com.buenosautos.buenosautosagendamento.solicitacao.dto;

import java.time.LocalDateTime;
import java.util.List;

// Importar as anotações de validação Jakarta
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;

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
    
    @NotBlank(message = "A placa do veículo é obrigatória") // Mantendo apenas NotBlank
    private String veiculoPlaca;

    // IDs dos serviços selecionados
    @NotNull(message = "Selecione pelo menos um serviço")
    @Size(min = 1, message = "Selecione pelo menos um serviço")
    private List<Long> servicoIds;

    // Dados do Agendamento
    @NotNull(message = "A data e hora do agendamento são obrigatórias")
    @FutureOrPresent(message = "A data do agendamento deve ser no presente ou futuro")
    private LocalDateTime dataAgendada;

    public SolicitacaoFormDTO() {}

    // Construtor completo (opcional, para testes ou inicialização programática)
    public SolicitacaoFormDTO(String clienteNome, String clienteCpf, String clienteEmail, String clienteTelefone,
            String veiculoMarca, String veiculoModelo, String veiculoAno, String veiculoPlaca, // NOVO PARÂMETRO
            List<Long> servicoIds, LocalDateTime dataAgendada) {
			this.clienteNome = clienteNome;
			this.clienteCpf = clienteCpf;
			this.clienteEmail = clienteEmail;
			this.clienteTelefone = clienteTelefone;
			this.veiculoMarca = veiculoMarca;
			this.veiculoModelo = veiculoModelo;
			this.veiculoAno = veiculoAno;
			this.veiculoPlaca = veiculoPlaca; // Inicializa o novo campo
			this.servicoIds = servicoIds;
			this.dataAgendada = dataAgendada;
			}

    // --- Getters e Setters para todos os campos ---
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
    public List<Long> getServicoIds() { return servicoIds; }
    public void setServicoIds(List<Long> servicoIds) { this.servicoIds = servicoIds; }
    public LocalDateTime getDataAgendada() { return dataAgendada; }
    public void setDataAgendada(LocalDateTime dataAgendada) { this.dataAgendada = dataAgendada; }
    
    public String getVeiculoPlaca() { // NOVO GETTER
        return veiculoPlaca;
    }

    public void setVeiculoPlaca(String veiculoPlaca) { // NOVO SETTER
        this.veiculoPlaca = veiculoPlaca;
    }

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
               ", servicoIds=" + servicoIds +
               ", dataAgendada=" + dataAgendada +
               '}';
    }
}