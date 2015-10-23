package se.leafcoders.rosette.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class RosetteAnonymousAuthenticationFilter extends GenericFilterBean {

    public RosetteAnonymousAuthenticationFilter() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			SecurityContextHolder.getContext().setAuthentication(
				new CurrentUserAuthentication()
			);
		}

        filterChain.doFilter(request, response);

        // Clear authentication when returning back from filter chain
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
