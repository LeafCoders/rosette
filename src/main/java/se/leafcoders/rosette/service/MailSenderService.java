package se.leafcoders.rosette.service;

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

    public void send(String to, String subject, String body) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(rosetteSettings.getDefaultMailFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
        } catch (MessagingException exception) {
            logError(subject, to, exception);
            return;
        } catch (MailSendException exception) {
            logError(subject, to, exception);
            return;
        }
        try {
            javaMailSender.send(message);
        } catch (MailException exception) {
            logError(subject, to, exception);
            return;
        }
    }

    private void logError(String type, String to, Exception e) {
        logger.error("Failed to send '{}' mail to '{}'. Reason: {}", type, to, e.getMessage());
    }
}
