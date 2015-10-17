package se.leafcoders.rosette.auth.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.leafcoders.rosette.auth.CurrentUser;
import se.leafcoders.rosette.auth.CurrentUserAuthentication;
import se.leafcoders.rosette.auth.CurrentUserService;
import se.leafcoders.rosette.model.PermissionTree;
import se.leafcoders.rosette.security.PermissionTreeHelper;

public class JwtAuthenticationService {

    private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

    private final JwtHandler tokenHandler;
    private CurrentUserService currentUserService;

    public JwtAuthenticationService(String secret, CurrentUserService currentUserService) {
        tokenHandler = new JwtHandler(secret, currentUserService);
        this.currentUserService = currentUserService;
    }

    public String addAuthentication(HttpServletResponse response, CurrentUserAuthentication authentication) {
        final CurrentUser user = (CurrentUser) authentication.getDetails();
        String token = tokenHandler.createTokenForUser(user);
        response.addHeader(AUTH_HEADER_NAME, token);
        return token;
    }

    public CurrentUserAuthentication getAuthentication(HttpServletRequest request) {
        final String token = request.getHeader(AUTH_HEADER_NAME);
        if (token != null) {
            final CurrentUser user = tokenHandler.parseUserFromToken(token);
            if (user != null) {
                PermissionTreeHelper ph = new PermissionTreeHelper();
                ph.create(currentUserService.getUserPermissions(user.getId()));
                PermissionTree tree = new PermissionTree();
                tree.setTree(ph.getTree());
                return new CurrentUserAuthentication(user, tree);
            }
        }
        return null;
    }
}
