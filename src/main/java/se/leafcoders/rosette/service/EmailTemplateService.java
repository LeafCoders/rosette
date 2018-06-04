package se.leafcoders.rosette.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import se.leafcoders.rosette.RosetteSettings;
import se.leafcoders.rosette.persistence.model.User;
import se.leafcoders.rosette.persistence.service.MessageService;

@Service
public class EmailTemplateService {

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private RosetteSettings rosetteSettings;

    private static final Logger logger = LoggerFactory.getLogger(EmailTemplateService.class);
    private final ClassLoader classLoader = getClass().getClassLoader();

    private final String templateTheme = "LeafCoders";

    public void sendForgottenPassword(User user, String token) {
        final String subject = messageService.lookup("forgottenPasswordEmail.title");
        
        Map<String, String> templateValues = new HashMap<>();
        templateValues.put("%%header.imageUrl%%", messageService.lookup("emailHeader.imageUrl"));
        templateValues.put("%%footer.row1%%", messageService.lookup("emailFooter.row1"));
        templateValues.put("%%footer.row2%%", messageService.lookup("emailFooter.row2"));
        templateValues.put("%%title.row1%%", subject);
        templateValues.put("%%title.row2%%", messageService.lookup("forgottenPasswordEmail.subTitle"));
        templateValues.put("%%content.helloUser%%", messageService.lookup("common.helloUser", user.getFirstName()));
        templateValues.put("%%content.row1%%", messageService.lookup("forgottenPasswordEmail.textRow1"));
        templateValues.put("%%content.row2%%", messageService.lookup("forgottenPasswordEmail.textRow2"));
        templateValues.put("%%content.setPasswordUrl%%", rosetteSettings.getCordateUrl() + "/#/auth/forgotten?token=" + token);
        templateValues.put("%%content.setPasswordTitle%%", messageService.lookup("forgottenPasswordEmail.changePassword"));

        String body = replaceAllTemplateValues(readTemplate("forgottenPassword.html"), templateValues);
        mailSenderService.send(user.getEmail(), subject, body);
    }

    public void sendChangedPasswordNotification(User user) {
        final String subject = messageService.lookup("changedPasswordEmail.title");
        
        Map<String, String> templateValues = new HashMap<>();
        templateValues.put("%%header.imageUrl%%", messageService.lookup("emailHeader.imageUrl"));
        templateValues.put("%%footer.row1%%", messageService.lookup("emailFooter.row1"));
        templateValues.put("%%footer.row2%%", messageService.lookup("emailFooter.row2"));
        templateValues.put("%%title.row1%%", subject);
        templateValues.put("%%title.row2%%", messageService.lookup("changedPasswordEmail.subTitle"));
        templateValues.put("%%content.helloUser%%", messageService.lookup("common.helloUser", user.getFirstName()));
        templateValues.put("%%content.row1%%", messageService.lookup("changedPasswordEmail.textRow1"));
        templateValues.put("%%content.row2%%", messageService.lookup("changedPasswordEmail.textRow2"));
        
        String body = replaceAllTemplateValues(readTemplate("changedPassword.html"), templateValues);
        mailSenderService.send(user.getEmail(), subject, body);
    }
    
    private String replaceAllTemplateValues(String rawTemplate, Map<String, String> templateValues) {
        for (String key : templateValues.keySet()) {
            rawTemplate = rawTemplate.replace(key, templateValues.get(key));
        }
        return rawTemplate;
    }

    private String readTemplate(String templateName) {
        File file = new File(classLoader.getResource("emailTemplates/" + templateTheme + "/" + templateName).getFile());
        try {
            return Files.toString(file, Charsets.UTF_8);
        } catch (IOException e) {
            logger.error("Email template file '" + templateTheme + "/" + templateName + "' was not found!");
            return "Something went wrong when generating this email. Please contact the administrator.";
        }
    }

}
