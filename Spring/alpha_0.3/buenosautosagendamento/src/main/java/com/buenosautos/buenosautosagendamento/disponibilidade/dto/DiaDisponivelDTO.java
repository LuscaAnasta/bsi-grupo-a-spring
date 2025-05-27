package com.buenosautos.buenosautosagendamento.disponibilidade.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter; // Para formatar o LocalTime para String
import java.util.ArrayList;
import java.util.List;

public class DiaDisponivelDTO {
    private LocalDate dia;
    private List<String> horariosDisponiveis; // Horários formatados como String "HH:MM"

    public DiaDisponivelDTO(LocalDate dia, List<LocalTime> horarios) {
        this.dia = dia;
        this.horariosDisponiveis = new ArrayList<>();
        // Converte cada LocalTime para String "HH:MM"
        for (LocalTime horario : horarios) {
            this.horariosDisponiveis.add(horario.format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }

    // --- Getters ---
    public LocalDate getDia() {
        return dia;
    }

    public List<String> getHorariosDisponiveis() {
        return horariosDisponiveis;
    }

    // --- Setters (Podem ser necessários para serialização/desserialização, mas não são usados diretamente aqui) ---
    public void setDia(LocalDate dia) {
        this.dia = dia;
    }

    public void setHorariosDisponiveis(List<String> horariosDisponiveis) {
        this.horariosDisponiveis = horariosDisponiveis;
    }

    @Override
    public String toString() {
        return "DiaDisponivelDTO{" +
               "dia=" + dia +
               ", horariosDisponiveis=" + horariosDisponiveis +
               '}';
    }
}