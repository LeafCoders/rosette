package se.leafcoders.rosette.service;

import java.text.MessageFormat;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.RosetteSettings;

@Service
public class MailSenderService {

    private static final Logger logger = LoggerFactory.getLogger(MailSenderService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private RosetteSettings rosetteSettings;

    public void sendToAdmin(String subject, String body) {
        send(rosetteSettings.getAdminMailTo(), subject, body);
    }
    
    public void send(String to, String subject, String body) {
        String from = rosetteSettings.getDefaultMailFrom();
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
        } catch (MessagingException exception) {
            logError(subject, from, to, exception);
            return;
        } catch (MailSendException exception) {
            logError(subject, from, to, exception);
            return;
        }
        try {
            javaMailSender.send(message);
            logger.info(MessageFormat.format("Send \"{0}\" mail from \"{1}\" to \"{2}\".", subject, from, to));
        } catch (MailException exception) {
            logError(subject, from, to, exception);
            return;
        }
    }

    private void logError(String type, String from, String to, Exception e) {
        logger.error(MessageFormat.format("Failed to send \"{0}\" mail from \"{1}\" to \"{2}\". Reason: {3}", type, from, to, e.getMessage()), e);
    }
}
