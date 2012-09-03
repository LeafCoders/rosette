package se.ryttargardskyrkan.rosette.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;

public class OptionalBasicHttpAuthenticationFilter extends BasicHttpAuthenticationFilter {

	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        boolean loggedIn = false; //false by default or we wouldn't be in this method
        if (isLoginAttempt(request, response)) {
            loggedIn = executeLogin(request, response);
            
            if (!loggedIn) {
              sendChallenge(request, response);
          }
        } else {
        	loggedIn = true;
        }
        return loggedIn;
    }

}
