package se.leafcoders.rosette.security;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.User;
import se.leafcoders.rosette.service.PermissionService;

@Service("mongoRealm")
public class MongoRealm extends AuthorizingRealm {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private PermissionService permissionService;

	PasswordMatcher passwordMatcher;

	@PostConstruct
	public void initialize() {
		setAuthenticationCachingEnabled(true);
		passwordMatcher = new RosettePasswordMatcher();
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		Set<String> permissions = new HashSet<String>();
		boolean isKnownUser = principalCollection.fromRealm("anonymousRealm").isEmpty();
		if (isKnownUser) {
			User user = (User)principalCollection.getPrimaryPrincipal();
			permissions.addAll(permissionService.getForUser(user.getId()));
		} else {
			permissions.addAll(permissionService.getForEveryone());
		}

		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		simpleAuthorizationInfo.setStringPermissions(permissions);
		return simpleAuthorizationInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
		if (authenticationToken instanceof UsernamePasswordToken) {
			SimpleAuthenticationInfo simpleAuthenticationInfo = null;

			UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
			String providedUsername = token.getUsername();

			final User user = mongoTemplate.findOne(Query.query(Criteria.where("email").is(providedUsername)), User.class);
			if (user != null) {
				simpleAuthenticationInfo = new SimpleAuthenticationInfo(user, user.getHashedPassword(), "mongoRealm");
			}

			return simpleAuthenticationInfo;
		} else if (authenticationToken instanceof AnonymousToken) {
			return new SimpleAuthenticationInfo("", "", "anonymousRealm");
		} else {
			throw new AuthenticationException("Unexpected '" + authenticationToken.getClass().getName() + "'. Expected UsernamePasswordToken instead.");
		}
	}

	@Override
	public CredentialsMatcher getCredentialsMatcher() {
		return passwordMatcher;
	}

	@Override
	public boolean supports(AuthenticationToken token) {
		boolean isSupporting = false;

		if (token instanceof AnonymousToken) {
			isSupporting = true;
		} else {
			isSupporting = super.supports(token);
		}

		return isSupporting;
	}

	public void clearCache(PrincipalCollection principals) {
		super.clearCache(principals);
		
		if (principals == null) {
			Cache<Object, AuthenticationInfo> authenticationCache = getAuthenticationCache();
			authenticationCache.clear();
			
			Cache<Object, AuthorizationInfo> authorizationCache = getAuthorizationCache();
			authorizationCache.clear();
		}
	}
}
