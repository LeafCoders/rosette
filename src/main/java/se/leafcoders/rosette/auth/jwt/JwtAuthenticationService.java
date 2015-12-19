package se.leafcoders.rosette.auth.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import se.leafcoders.rosette.auth.CurrentUser;
import se.leafcoders.rosette.auth.CurrentUserAuthentication;
import se.leafcoders.rosette.auth.CurrentUserService;

public class JwtAuthenticationService {

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
        final String token = request.getHeader(AUTH_HEADER_NAME);
        if (token != null) {
            final CurrentUser user = tokenHandler.parseUserFromToken(token);
            if (user != null) {
                return new CurrentUserAuthentication(user);
            }
        }
        return null;
    }
}
