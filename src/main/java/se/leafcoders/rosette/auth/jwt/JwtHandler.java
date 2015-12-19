package se.leafcoders.rosette.auth.jwt;

import com.google.common.base.Preconditions;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import se.leafcoders.rosette.auth.CurrentUser;
import se.leafcoders.rosette.auth.CurrentUserService;

public final class JwtHandler {

    private final String jwtSecret;
    private final CurrentUserService userService;

    public JwtHandler(String jwtSecret, CurrentUserService userService) throws IllegalArgumentException {
        Preconditions.checkArgument(jwtSecret != null && jwtSecret.trim().length() > 10, "JwtToken: Invalid or too short token");
        this.jwtSecret = jwtSecret;
        this.userService = Preconditions.checkNotNull(userService);
    }

    public CurrentUser parseUserFromToken(String token) {
        try {
            String userId = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
            return userService.loadUserById(userId);
        } catch (io.jsonwebtoken.SignatureException ignore) {
            return null;
        }
    }

    public String createTokenForUser(CurrentUser user) {
        return Jwts.builder().setSubject(user.getId()).signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }
}
