package se.leafcoders.rosette.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;

import se.leafcoders.rosette.security.AnonymousToken;

public class OptionalBasicHttpAuthenticationFilter extends BasicHttpAuthenticationFilter {

	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        boolean loggedIn = false; //false by default or we wouldn't be in this method
        if (isLoginAttempt(request, response)) {
            loggedIn = executeLogin(request, response);
            
            if (!loggedIn) {
              sendChallenge(request, response);
          }
        } else {
        	HttpServletRequest httpRequest = (HttpServletRequest) request;
        	httpRequest.setAttribute("anonymousSubject", true);
        	
        	Subject subject = getSubject(request, response);
            AuthenticationToken token = new AnonymousToken();
			subject.login(token);
			loggedIn = onLoginSuccess(token, subject, request, response);
        }
        return loggedIn;
    }

}
