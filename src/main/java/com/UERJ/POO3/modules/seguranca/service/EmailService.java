package com.UERJ.POO3.modules.seguranca.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailRecuperacao(String destinatario, String token) {
        SimpleMailMessage mensagem = new SimpleMailMessage();

        // O e-mail de quem está enviando (pode ser fictício no Mailtrap)
        mensagem.setFrom("nao-responda@petshoperp.com.br");
        mensagem.setTo(destinatario);
        mensagem.setSubject("Petshop ERP - Recuperação de Senha");

        // Corpo do e-mail
        mensagem.setText("Olá!\n\n" +
                "Recebemos uma solicitação para redefinir a senha da sua conta.\n" +
                "Utilize o token de segurança abaixo no sistema para criar uma nova senha:\n\n" +
                token + "\n\n" +
                "Atenção: Este token é válido por apenas 1 hora.\n" +
                "Se você não solicitou esta alteração, ignore este e-mail.");

        mailSender.send(mensagem);

        System.out.println("E-mail de recuperação enviado para: " + destinatario);
    }
    public void enviarEmailAlertaEstoque(String destinatario, String assunto, String texto){
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom("nao-responda@petshoperp.com.br");
        mensagem.setTo(destinatario);
        mensagem.setSubject(assunto);
        mensagem.setText(texto);
        mailSender.send(mensagem);
    }
}
