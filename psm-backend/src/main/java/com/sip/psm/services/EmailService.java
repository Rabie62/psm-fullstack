package com.sip.psm.services;

import com.sip.psm.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service d'envoi d'emails asynchrone (@Async) pour ne jamais bloquer le thread HTTP lors d'une inscription.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;

    @Value("${psm.admin.email:admin@smartsoft-solutions.tn}")
    private String adminEmail;

    @Value("${spring.mail.username:noreply@smartsoft-solutions.tn}")
    private String senderEmail;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void notifyAdminOnRegistration(User newUser) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(senderEmail);
            msg.setTo(adminEmail);
            msg.setSubject("[PSM - SmartSoft Solutions] Nouvelle inscription utilisateur : "
                    + newUser.getPrenom() + " " + newUser.getNom());
            msg.setText("Bonjour Administrateur,\n\n"
                    + "Un nouvel utilisateur vient de s'inscrire sur la plateforme Projects Manager (PSM) :\n"
                    + "- Nom complet : " + newUser.getPrenom() + " " + newUser.getNom() + "\n"
                    + "- Email : " + newUser.getEmail() + "\n"
                    + "- Téléphone : " + newUser.getTel() + "\n\n"
                    + "Cordialement,\nSystème de Notification Asynchrone PSM");
            javaMailSender.send(msg);
            log.info("[Async] Email de notification envoyé à l'admin ({}) pour {}", adminEmail, newUser.getEmail());
        } catch (Exception ex) {
            log.warn("[Async] Impossible d'envoyer l'email SMTP : {}", ex.getMessage());
        }
    }

    @Async
    public void sendAccountStatusEmail(String userEmail, boolean isActivated) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(senderEmail);
            msg.setTo(userEmail);
            msg.setSubject(isActivated ? "[PSM] Votre compte a été activé" : "[PSM] Votre compte a été désactivé");
            msg.setText(isActivated
                    ? "Bonjour,\n\nVotre compte sur Projects Manager (PSM) est actif.\nCordialement,\nSmartSoft Solutions"
                    : "Bonjour,\n\nVotre compte sur Projects Manager (PSM) a été désactivé.\nCordialement,\nSmartSoft Solutions");
            javaMailSender.send(msg);
        } catch (Exception ex) {
            log.warn("[Async] Notification email statut compte non envoyée : {}", ex.getMessage());
        }
    }
}
