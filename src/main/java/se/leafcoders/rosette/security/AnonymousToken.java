package se.ryttargardskyrkan.rosette.security;

import org.apache.shiro.authc.AuthenticationToken;

@SuppressWarnings("serial")
public class AnonymousToken implements AuthenticationToken {

	@Override
	public Object getPrincipal() {
		return null;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

}
