package se.leafcoders.rosette.service;

import java.util.Base64;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.auth.jwt.JwtAuthenticationService;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.model.ForgottenPassword;
import se.leafcoders.rosette.model.User;
import se.leafcoders.rosette.model.error.ValidationError;

@Service
public class ForgottenPasswordService {

    private static final Logger logger = LoggerFactory.getLogger(ForgottenPasswordService.class);

    @Autowired
    private DbService<ForgottenPassword> dbForgotten;

    @Autowired
    private DbService<User> dbUser;

    @Autowired
    private UserService userService;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private JwtAuthenticationService jwtAuthenticationService;

    public ForgottenPassword create(String email) {
        User user = dbUser.findBy("email", email);
        if (user != null) {
            ForgottenPassword forgottenPassword = new ForgottenPassword();
            forgottenPassword.setToken(jwtAuthenticationService.createTokenForUser(user));
            forgottenPassword.setUserId(user.getId());
            dbForgotten.create(forgottenPassword);

            final String emailSubject = "Glömt ditt lösenord?";
            final String emailBody = "Här kommer din kod: " + forgottenPassword.getToken();
            try {
                sendMailService.send(user.getEmail(), emailSubject, emailBody);
                return forgottenPassword;
            } catch (MessagingException e) {
                logger.error("Failed to send email to '{}' with subject '{}'. Reason: {}", user.getEmail(), emailSubject, e.getMessage());
            }
        }
        return null;
    }

    public boolean apply(String token, String password) {
        ForgottenPassword forgottenPassword = dbForgotten.findBy("token", token);
        try {
            if (forgottenPassword != null) {
                password = new String(Base64.getUrlDecoder().decode(password));
                if (userService.changePassword(forgottenPassword.getUserId(), password)) {
                    dbForgotten.deleteById(forgottenPassword.getId());
                    return true;
                }
            }
        } catch (IllegalArgumentException ignore) {
            throw new SimpleValidationException(new ValidationError("password", "Password must be base64 url encoded"));            
        }
        catch (NotFoundException ignore) {}
        throw new NotFoundException("ForgottenPassword", token);
    }
}
