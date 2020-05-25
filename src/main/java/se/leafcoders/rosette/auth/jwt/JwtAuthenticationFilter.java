package se.leafcoders.rosette.auth.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import se.leafcoders.rosette.auth.CurrentUserAuthentication;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends GenericFilterBean {

    @NonNull
    private final JwtAuthenticationService jwtAuthenticationService;

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
