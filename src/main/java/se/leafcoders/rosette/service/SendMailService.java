package se.leafcoders.rosette.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.config.RosetteSettings;

@Service
public class SendMailService {

    private static final Logger logger = LoggerFactory.getLogger(SendMailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private RosetteSettings rosetteSettings;
    
    public void send(String to, String subject, String body) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(rosetteSettings.getDefaultMailFrom());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        
        try {
            javaMailSender.send(message);
        } catch (MailSendException e) {
            logger.error("Failed to send mail to '{}'. Reason: {}", to, e.getMessage());
        }
    }
}