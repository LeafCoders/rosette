package se.leafcoders.rosette.controller;

import java.text.MessageFormat;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.RosetteSettings;
import se.leafcoders.rosette.controller.dto.HelpRequestIn;
import se.leafcoders.rosette.service.MailSenderService;
import se.leafcoders.rosette.util.HtmlSanitize;

@Transactional
@RestController
@RequestMapping(value = "api/sendmessages", produces = "application/json")
public class SendMessagesController {

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private RosetteSettings rosetteSettings;

    @PostMapping(value = "/helprequest", consumes = "application/json")
    public void sendHelpRequestMessage(@RequestBody HelpRequestIn data) {
        String fromName = HtmlSanitize.sanitize(data.getFromName());
        String fromEmail = HtmlSanitize.sanitize(data.getFromEmail());
        String message = HtmlSanitize.sanitizeAndConvertNewline(data.getMessage());
        String body = MessageFormat.format("<p>{0} ({1}) vill ha hjälp med följande:</p><hr><p><em>{2}</em></p>", fromName, fromEmail, message);
        mailSenderService.send(rosetteSettings.getAdminMailTo(), data.getSubject(), body);
    }

}
