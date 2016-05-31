package se.leafcoders.rosette.service;

import java.io.File;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import se.leafcoders.rosette.config.RosetteSettings;

@Service
public class SendMailService {

    private static final Logger logger = LoggerFactory.getLogger(SendMailService.class);
    private final ClassLoader classLoader = getClass().getClassLoader();

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private RosetteSettings rosetteSettings;
    
    public void forgottenPassword(String to, String fullName, String token) {
        try {
            File file = new File(classLoader.getResource("emailtemplates/ForgottenPassword.html").getFile());
            String data = Files.toString(file, Charsets.UTF_8);
            data = data
                    .replace("###logo-image###", "http://home.page/header-logo.png")
                    .replace("###logo-link###", "http://home.page")
                    .replace("###header-title###", "Glömt lösenord?")
                    .replace("###header-image###", "http://placehold.it/400x250")
                    .replace("###content-title###", fullName)
                    .replace("###content-body###", "Nu kan du byta ditt lösenord.")
                    .replace("###button-title###", "Byt lösenord")
                    .replace("###button-link###", rosetteSettings.getCordateUrl() + "/auth/forgotten?token=" + token)
                    .replace("###footer-text###", "Made by LeafCoders");
            
            send(to, "Glömt ditt lösenord?", data);
        } catch (Exception e) {
            logError("ForgottenPassword", to, e);
        }
    }
    
    private void send(String to, String subject, String body) throws MessagingException, MailSendException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(rosetteSettings.getDefaultMailFrom());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        javaMailSender.send(message);
    }

    private void logError(String type, String to, Exception e) {
        logger.error("Failed to send '{}' mail to '{}'. Reason: {}", type, to, e.getMessage());
    }
}