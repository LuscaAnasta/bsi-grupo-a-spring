package com.buenosautos.buenosautosagendamento.disponibilidade.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime; // Para os horários de início e fim do dia

@Entity
public class Disponibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate dia; // O dia que está sendo gerenciado, ex: 2025-05-30

    @Column(nullable = false)
    private LocalTime horarioInicio; // Ex: 09:00 (início do período de trabalho do dia)

    @Column(nullable = false)
    private LocalTime horarioFim;    // Ex: 17:00 (fim do período de trabalho do dia)

    @Column(nullable = false)
    private int capacidadeMaximaDia; // Ex: 4 (capacidade total de veículos para este dia)

    @Column(nullable = false)
    private int agendamentosNoDia; // Contagem total de agendamentos para o dia

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDisponibilidadeDiaria statusDia; // Status gerencial do dia (aberto/fechado pelo usuário)

    public enum StatusDisponibilidadeDiaria {
        ABERTO,       // O dia está disponível para agendamento
        FECHADO       // O dia foi bloqueado pelo usuário (feriado, manutenção, etc.)
    }

    public Disponibilidade() {
        this.agendamentosNoDia = 0;
        this.statusDia = StatusDisponibilidadeDiaria.ABERTO;
    }

    public Disponibilidade(LocalDate dia, LocalTime horarioInicio, LocalTime horarioFim, int capacidadeMaximaDia) {
        this();
        this.dia = dia;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.capacidadeMaximaDia = capacidadeMaximaDia;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDia() { return dia; }
    public void setDia(LocalDate dia) { this.dia = dia; }
    public LocalTime getHorarioInicio() { return horarioInicio; }
    public void setHorarioInicio(LocalTime horarioInicio) { this.horarioInicio = horarioInicio; }
    public LocalTime getHorarioFim() { return horarioFim; }
    public void setHorarioFim(LocalTime horarioFim) { this.horarioFim = horarioFim; }
    public int getCapacidadeMaximaDia() { return capacidadeMaximaDia; }
    public void setCapacidadeMaximaDia(int capacidadeMaximaDia) { this.capacidadeMaximaDia = capacidadeMaximaDia; }
    public int getAgendamentosNoDia() { return agendamentosNoDia; }
    public void setAgendamentosNoDia(int agendamentosNoDia) { this.agendamentosNoDia = agendamentosNoDia; }
    public StatusDisponibilidadeDiaria getStatusDia() { return statusDia; }
    public void setStatusDia(StatusDisponibilidadeDiaria statusDia) { this.statusDia = statusDia; }

    // Métodos auxiliares para a capacidade diária
    public boolean estaCheio() {
        return this.agendamentosNoDia >= this.capacidadeMaximaDia;
    }

    public boolean estaAbertoParaNovosAgendamentos() {
        return this.statusDia == StatusDisponibilidadeDiaria.ABERTO && !estaCheio();
    }

    // Método para verificar se o dia já passou completamente (apenas a data)
    public boolean jaPassou() {
        return LocalDate.now().isAfter(this.dia);
    }
    
    // Método para verificar se o dia já passou o horário de término
    public boolean jaPassouHorarioFinal() {
        // Combina a data da disponibilidade com a hora de término para verificar se já passou
        return LocalDateTime.now().isAfter(this.dia.atTime(this.horarioFim));
    }


    public void incrementarAgendamentoNoDia() {
        if (agendamentosNoDia < capacidadeMaximaDia) {
            this.agendamentosNoDia++;
        } else {
            throw new IllegalStateException("Capacidade máxima do dia " + this.dia + " atingida.");
        }
    }

    public void decrementarAgendamentoNoDia() {
        if (agendamentosNoDia > 0) {
            this.agendamentosNoDia--;
        }
    }
}