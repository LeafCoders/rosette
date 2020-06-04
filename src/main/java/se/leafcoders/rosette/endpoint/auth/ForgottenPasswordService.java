package se.leafcoders.rosette.endpoint.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.leafcoders.rosette.auth.jwt.JwtAuthenticationService;
import se.leafcoders.rosette.core.exception.NotFoundException;
import se.leafcoders.rosette.core.service.EmailTemplateService;
import se.leafcoders.rosette.endpoint.user.User;
import se.leafcoders.rosette.endpoint.user.UserRepository;
import se.leafcoders.rosette.endpoint.user.UserService;

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
            emailTemplateService.sendForgottenPasswordEmail(user, forgottenPassword.getToken());
            return forgottenPassword;
        }
        return null;
    }

    public boolean apply(String token, String password) {
        ForgottenPassword forgottenPassword = forgottenPasswordRepository.findByToken(token);
        if (forgottenPassword != null) {
            User user = userService.changePassword(forgottenPassword.getUserId(), password);
            forgottenPasswordRepository.delete(forgottenPassword);
            emailTemplateService.sendChangedPasswordEmail(user);
            return true;
        }
        throw new NotFoundException("Token for 'forgotten password' was not found: " + token);
    }

}
