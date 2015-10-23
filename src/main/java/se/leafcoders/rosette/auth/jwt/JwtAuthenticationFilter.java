package se.leafcoders.rosette.auth.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.google.common.base.Preconditions;

import se.leafcoders.rosette.auth.CurrentUserAuthentication;

public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtAuthenticationService jwtAuthenticationService;

    public JwtAuthenticationFilter(JwtAuthenticationService jwtAuthenticationService) {
        this.jwtAuthenticationService = Preconditions.checkNotNull(jwtAuthenticationService);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        CurrentUserAuthentication authentication = jwtAuthenticationService
                .createAuthentication((HttpServletRequest) request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);

        // Clear authentication when returning back from filter chain
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
