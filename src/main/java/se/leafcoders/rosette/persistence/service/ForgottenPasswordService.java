package se.leafcoders.rosette.persistence.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.auth.jwt.JwtAuthenticationService;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.persistence.model.ForgottenPassword;
import se.leafcoders.rosette.persistence.model.User;
import se.leafcoders.rosette.persistence.repository.ForgottenPasswordRepository;
import se.leafcoders.rosette.persistence.repository.UserRepository;
import se.leafcoders.rosette.service.EmailTemplateService;

@Service
public class ForgottenPasswordService {

    @Autowired
    private ForgottenPasswordRepository forgottenPasswordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailTemplateService emailTemplateService;

    @Autowired
    private JwtAuthenticationService jwtAuthenticationService;

    public ForgottenPassword create(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            ForgottenPassword forgottenPassword = new ForgottenPassword();
            forgottenPassword.setUserId(user.getId());
            forgottenPassword.setToken(jwtAuthenticationService.createTokenForForgottenPassword(user));
            forgottenPasswordRepository.save(forgottenPassword);
            emailTemplateService.sendForgottenPassword(user, forgottenPassword.getToken());
            return forgottenPassword;
        }
        return null;
    }

    public boolean apply(String token, String password) {
        ForgottenPassword forgottenPassword = forgottenPasswordRepository.findByToken(token);
        if (forgottenPassword != null) {
            User user = userService.changePassword(forgottenPassword.getUserId(), password);
            forgottenPasswordRepository.delete(forgottenPassword);
            emailTemplateService.sendChangedPasswordNotification(user);
            return true;
        }
        throw new NotFoundException("Token for 'forgotten password' was not found: " + token);
    }

}
