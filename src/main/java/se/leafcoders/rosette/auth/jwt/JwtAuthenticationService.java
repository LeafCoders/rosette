package se.leafcoders.rosette.auth.jwt;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.leafcoders.rosette.auth.CurrentUser;
import se.leafcoders.rosette.auth.CurrentUserAuthentication;
import se.leafcoders.rosette.auth.CurrentUserService;
import se.leafcoders.rosette.endpoint.user.User;

public class JwtAuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationService.class);

    private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

    private final JwtHandler tokenHandler;

    public JwtAuthenticationService(String secret, CurrentUserService currentUserService) {
        tokenHandler = new JwtHandler(secret, currentUserService);
    }

    public String addAuthenticationHeader(HttpServletResponse response, CurrentUserAuthentication authentication) {
        final CurrentUser user = (CurrentUser) authentication.getDetails();
        String token = tokenHandler.createTokenForUser(user);
        response.addHeader(AUTH_HEADER_NAME, token);
        return token;
    }

    public CurrentUserAuthentication createAuthentication(HttpServletRequest request) {
        String token = request.getHeader(AUTH_HEADER_NAME);
        if (token == null) {
            token = request.getParameter(AUTH_HEADER_NAME);
        }
        if (token != null) {
            final CurrentUser user = tokenHandler.parseUserFromToken(token);
            if (user != null) {
                return new CurrentUserAuthentication(user);
            }
            logger.info(MessageFormat.format("Requst had an invalid token \"{0}\". Request will be handled as anonymous.", token));
        }
        return null;
    }

    public String createTokenForUser(User user) {
        return tokenHandler.createTokenForUserId(user.getId());
    }

    public String createTokenForForgottenPassword(User user) {
        return tokenHandler.createTokenForForgottenPassword(user.getFirstName());
    }
}
