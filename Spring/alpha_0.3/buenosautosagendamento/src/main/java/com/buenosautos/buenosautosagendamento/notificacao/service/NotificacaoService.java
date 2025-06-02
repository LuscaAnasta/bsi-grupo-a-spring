package com.buenosautos.buenosautosagendamento.notificacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {

    @Autowired
    private JavaMailSender mailSender;

    // Método de confirmação de agendamento (solicitação inicial)
    // Usado pelo SolicitacaoService
    public void sendConfirmationEmail(String toEmail, String clientName, String serviceDetails, String appointmentDate, String appointmentTime) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("seu-email@gmail.com"); // Mantenha o seu email configurado aqui
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
    // Usado pelo ProtocoloService.confirmarProtocolo()
    public void sendAppointmentConfirmedByMechanicEmail(String toEmail, String clientName, String serviceDetails, String appointmentDate, String appointmentTime, String protocolNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("seu-email@gmail.com"); // Mantenha o seu email configurado aqui
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
    // Usado pelo ProtocoloService.concluirProtocolo()
    public void sendServiceConcludedWaitingPickupEmail(String toEmail, String clientName, String vehicleDetails, String serviceDetails, String protocolNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("seu-email@gmail.com"); // Mantenha o seu email configurado aqui
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

    // NOVO MÉTODO: Para notificar que o protocolo foi cancelado
    // Usado pelo ProtocoloService.cancelarProtocolo()
    public void sendProtocolCanceledEmail(String toEmail, String clientName, String protocolNumber, String vehicleDetails, String reasonForCancellation, String mechanicObservation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("seu-email@gmail.com"); // Mantenha o seu email configurado aqui
        message.setTo(toEmail);
        message.setSubject("Seu Agendamento/Serviço Foi Cancelado - Buenos Autos");

        String emailBody = String.format(
            "Olá %s,\n\n" +
            "Informamos que o seu agendamento/serviço referente ao veículo %s (Protocolo: %s) foi CANCELADO.\n\n" +
            "Motivo do Cancelamento: %s\n\n" +
            (mechanicObservation != null && !mechanicObservation.trim().isEmpty() ? "Observação do Mecânico: %s\n\n" : "") + // Adiciona observação se não for vazia
            "Se tiver alguma dúvida ou desejar reagendar, por favor, entre em contato conosco.\n\n" +
            "Atenciosamente,\n" +
            "Equipe Buenos Autos",
            clientName, vehicleDetails, protocolNumber, reasonForCancellation, mechanicObservation // Passe mechanicObservation aqui
        );
        message.setText(emailBody);
        sendEmail(message, toEmail, "Protocolo Cancelado");
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
    
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("seu-email@gmail.com"); // Mantenha o seu email configurado aqui
        message.setTo(toEmail);
        message.setSubject("Redefinição de Senha - Buenos Autos");

        String emailBody = String.format(
            "Olá,\n\n" +
            "Recebemos uma solicitação para redefinir a senha da sua conta na Buenos Autos.\n\n" +
            "Para redefinir sua senha, clique no link abaixo:\n" +
            "%s\n\n" +
            "Este link é válido por 30 minutos. Se você não solicitou a redefinição de senha, por favor, ignore este e-mail.\n\n" +
            "Atenciosamente,\n" +
            "Equipe Buenos Autos",
            resetLink
        );
        message.setText(emailBody);
        sendEmail(message, toEmail, "Redefinição de Senha");
    }
}