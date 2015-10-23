package se.leafcoders.rosette.auth.jwt;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.leafcoders.rosette.auth.CurrentUser;
import se.leafcoders.rosette.auth.CurrentUserAuthentication;

public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final JwtAuthenticationService jwtAuthenticationService;

    public JwtLoginFilter(String urlMapping, JwtAuthenticationService tokenAuthenticationService,
            AuthenticationManager authManager) {
        super(new AntPathRequestMatcher(urlMapping));
        this.jwtAuthenticationService = tokenAuthenticationService;
        setAuthenticationManager(authManager);
    }

    // TODO: Remove this method and extend UsernamePasswordAuthenticationFilter instead, if possible...
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (username == null || password == null) {
            throw new BadCredentialsException("Username and password must be specified");
        }

        username = username.trim();
        try {
            password = new String(Base64.getUrlDecoder().decode(password));
        } catch (IllegalArgumentException ignore) {
            throw new BadCredentialsException("Password must be base64 url encoded");
        }

        UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(username, password);
        return getAuthenticationManager().authenticate(loginToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authentication) throws IOException, ServletException {

        final CurrentUser authenticatedUser = (CurrentUser) authentication.getPrincipal();
        final CurrentUserAuthentication userAuthentication = new CurrentUserAuthentication(authenticatedUser);

        // Add the custom token as HTTP header to the response
        jwtAuthenticationService.addAuthenticationHeader(response, userAuthentication);
        
        // Add the authentication to the Security context
        // TODO: Maybe we don't need to do this. Login will only return an empty document so we need no authentication...
        SecurityContextHolder.getContext().setAuthentication(userAuthentication);

        HashMap<String, String> userData = new HashMap<String, String>();
        CurrentUser currentUser = (CurrentUser) userAuthentication.getDetails();
        userData.put("id", currentUser.getId());
        userData.put("fullName", currentUser.getFullName());
        userData.put("email", currentUser.getUsername());
        response.getOutputStream().print(new ObjectMapper().writeValueAsString(userData));
        response.flushBuffer();
    }
}
