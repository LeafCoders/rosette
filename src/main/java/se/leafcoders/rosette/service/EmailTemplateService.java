package se.leafcoders.rosette.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public void sendForgottenPasswordEmail(User user, String token) {
        final String subject = messageService.lookup("forgottenPasswordEmail.title");
        
        Map<String, String> templateValues = defaultTemplateValues();
        templateValues.put("%%title.row1%%", subject);
        templateValues.put("%%title.row2%%", messageService.lookup("forgottenPasswordEmail.subTitle"));
        templateValues.put("%%content.helloUser%%", messageService.lookup("common.helloUser", user.getFirstName()));
        templateValues.put("%%content.row1%%", messageService.lookup("forgottenPasswordEmail.textRow1"));
        templateValues.put("%%content.row2%%", messageService.lookup("forgottenPasswordEmail.textRow2"));
        templateValues.put("%%content.setPasswordUrl%%", rosetteSettings.getCordateUrl() + "/#/auth/forgotten?token=" + token);
        templateValues.put("%%content.setPasswordTitle%%", messageService.lookup("forgottenPasswordEmail.changePassword"));

        String body = replaceAllTemplateValues(readTemplate("forgottenPasswordEmail.html"), templateValues);
        mailSenderService.send(user.getEmail(), subject, body);
    }

    public void sendChangedPasswordEmail(User user) {
        final String subject = messageService.lookup("changedPasswordEmail.title");
        
        Map<String, String> templateValues = defaultTemplateValues();
        templateValues.put("%%title.row1%%", subject);
        templateValues.put("%%title.row2%%", messageService.lookup("changedPasswordEmail.subTitle"));
        templateValues.put("%%content.helloUser%%", messageService.lookup("common.helloUser", user.getFirstName()));
        templateValues.put("%%content.row1%%", messageService.lookup("changedPasswordEmail.textRow1"));
        
        String body = replaceAllTemplateValues(readTemplate("changedPasswordEmail.html"), templateValues);
        mailSenderService.send(user.getEmail(), subject, body);
    }

    public void sendWelcomeEmail(User user) {
        final String subject = messageService.lookup("welcomeEmail.title");
        
        Map<String, String> templateValues = defaultTemplateValues();
        templateValues.put("%%title.row1%%", subject);
        templateValues.put("%%title.row2%%", messageService.lookup("welcomeEmail.subTitle"));
        templateValues.put("%%content.helloUser%%", messageService.lookup("common.helloUser", user.getFirstName()));
        templateValues.put("%%content.row1%%", messageService.lookup("welcomeEmail.textRow1"));

        String body = replaceAllTemplateValues(readTemplate("welcomeEmail.html"), templateValues);
        mailSenderService.send(user.getEmail(), subject, body);
    }

    public void sendActivatedUserEmail(User user) {
        final String subject = messageService.lookup("activatedUserEmail.title");
        
        Map<String, String> templateValues = defaultTemplateValues();
        templateValues.put("%%title.row1%%", subject);
        templateValues.put("%%title.row2%%", messageService.lookup("activatedUserEmail.subTitle"));
        templateValues.put("%%content.helloUser%%", messageService.lookup("common.helloUser", user.getFirstName()));
        templateValues.put("%%content.row1%%", messageService.lookup("activatedUserEmail.textRow1"));
        templateValues.put("%%content.loginUrl%%", rosetteSettings.getCordateUrl() + "/#/auth/login?username=" + user.getEmail());
        templateValues.put("%%content.loginTitle%%", messageService.lookup("activatedUserEmail.login"));

        String body = replaceAllTemplateValues(readTemplate("activatedUserEmail.html"), templateValues);
        mailSenderService.send(user.getEmail(), subject, body);
    }

    private Map<String, String> defaultTemplateValues() {
        Map<String, String> templateValues = new HashMap<>();
        templateValues.put("%%header.imageUrl%%", messageService.lookup("emailHeader.imageUrl"));
        templateValues.put("%%footer.row1%%", messageService.lookup("emailFooter.row1"));
        templateValues.put("%%footer.row2%%", messageService.lookup("emailFooter.row2"));
        return templateValues;
    }

    private String replaceAllTemplateValues(String rawTemplate, Map<String, String> templateValues) {
        for (String key : templateValues.keySet()) {
            rawTemplate = rawTemplate.replace(key, templateValues.get(key));
        }
        return rawTemplate;
    }

    private String readTemplate(String templateName) {
        InputStream inputStream = null;
        try {
            inputStream = classLoader.getResourceAsStream("emailTemplates/" + templateTheme + "/" + templateName);
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } catch (IOException e) {
            logger.error(MessageFormat.format("Email template file \"{0}/{1}\" was not found!", templateTheme, templateName), e);
            return "Something went wrong when generating this email. Please contact the administrator.";
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

}
