package se.leafcoders.rosette.auth.jwt;

import java.util.Date;
import com.google.common.base.Preconditions;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import se.leafcoders.rosette.auth.CurrentUser;
import se.leafcoders.rosette.auth.CurrentUserService;

public final class JwtHandler {

    private static long VALID_LENGTH = 14 * 24 * 60 * 60 * 1000;
    private static long FORGOTTENPASSWORD_VALID_LENGTH = 30 * 60 * 1000;

    private final String jwtSecret;
    private final CurrentUserService userService;

    public JwtHandler(String jwtSecret, CurrentUserService userService) throws IllegalArgumentException {
        Preconditions.checkArgument(jwtSecret != null && jwtSecret.trim().length() > 10,
                "JwtToken: Invalid or too short token");
        this.jwtSecret = jwtSecret;
        this.userService = Preconditions.checkNotNull(userService);
    }

    public CurrentUser parseUserFromToken(String token) {
        try {
            String userId = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
            if (userId != null) {
                return userService.loadUserById(Long.parseLong(userId));
            }
        } catch (io.jsonwebtoken.SignatureException | io.jsonwebtoken.MalformedJwtException ignore) {
        }
        return null;
    }

    public String createTokenForUser(CurrentUser user) {
        return createTokenForUserId(user.getId());
    }

    public String createTokenForUserId(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(new Date(System.currentTimeMillis() + VALID_LENGTH))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String createTokenForForgottenPassword(String nameOfUser) {
        return Jwts.builder()
                .setSubject(nameOfUser)
                .setExpiration(new Date(System.currentTimeMillis() + FORGOTTENPASSWORD_VALID_LENGTH))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}
