package com.buenosautos.buenosautosagendamento.notificacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {

    @Autowired
    private JavaMailSender mailSender;

    // Método original de confirmação de agendamento (mantido para a solicitação inicial)
    public void sendConfirmationEmail(String toEmail, String clientName, String serviceDetails, String appointmentDate, String appointmentTime) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("seu-email@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Confirmação de Agendamento - Buenos Autos");

        String emailBody = String.format(
            "Olá %s,\n\n" +
            "Sua solicitação de agendamento na Buenos Autos foi recebida e será avaliada!\n\n" +
            "Detalhes do Agendamento:\n" +
            "Serviços: %s\n" +
            "Data: %s\n" +
            "Horário: %s\n\n" +
            "Aguarde a confirmação do mecânico. Você receberá um novo e-mail com o número do protocolo assim que confirmado.\n\n" +
            "Atenciosamente,\n" +
            "Equipe Buenos Autos",
            clientName, serviceDetails, appointmentDate, appointmentTime
        );
        message.setText(emailBody);
        sendEmail(message, toEmail, "Confirmação de Solicitação Inicial");
    }

    // NOVO MÉTODO: E-mail de Agendamento Confirmado pelo Mecânico com Protocolo
    public void sendAppointmentConfirmedByMechanicEmail(String toEmail, String clientName, String serviceDetails, String appointmentDate, String appointmentTime, String protocolNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("seu-email@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Seu Agendamento foi Confirmado! - Buenos Autos");

        String emailBody = String.format(
            "Olá %s,\n\n" +
            "Ótimas notícias! Seu agendamento na Buenos Autos foi CONFIRMADO pelo nosso mecânico!\n\n" +
            "Detalhes do Agendamento:\n" +
            "Serviços: %s\n" +
            "Data: %s\n" +
            "Horário: %s\n" +
            "Número do Protocolo: %s\n\n" +
            "Por favor, chegue no horário agendado com seu veículo.\n\n" +
            "Atenciosamente,\n" +
            "Equipe Buenos Autos",
            clientName, serviceDetails, appointmentDate, appointmentTime, protocolNumber
        );
        message.setText(emailBody);
        sendEmail(message, toEmail, "Agendamento Confirmado pelo Mecânico");
    }

    // Método para notificar que o serviço está concluído e aguardando retirada
    public void sendServiceConcludedWaitingPickupEmail(String toEmail, String clientName, String vehicleDetails, String serviceDetails, String protocolNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("seu-email@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Seu Veículo Está Pronto para Retirada - Buenos Autos");

        String emailBody = String.format(
            "Olá %s,\n\n" +
            "Temos ótimas notícias! O serviço em seu veículo (%s) foi concluído e ele está pronto para retirada em nossa oficina.\n\n" +
            "Serviços Realizados: %s\n" +
            "Número do Protocolo: %s\n\n" +
            "Por favor, venha retirar seu veículo o mais breve possível.\n\n" +
            "Atenciosamente,\n" +
            "Equipe Buenos Autos",
            clientName, vehicleDetails, serviceDetails, protocolNumber
        );
        message.setText(emailBody);
        sendEmail(message, toEmail, "Serviço Concluído - Aguardando Retirada");
    }

    // Método auxiliar para enviar o e-mail e tratar exceções
    private void sendEmail(SimpleMailMessage message, String toEmail, String subject) {
        try {
            mailSender.send(message);
            System.out.println("E-mail enviado com sucesso (" + subject + ") para: " + toEmail);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail (" + subject + ") para " + toEmail + ": " + e.getMessage());
            // Considere adicionar lógica de retry ou notificação de falha aqui.
        }
    }
}