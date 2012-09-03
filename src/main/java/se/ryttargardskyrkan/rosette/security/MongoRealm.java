package se.ryttargardskyrkan.rosette.security;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.ryttargardskyrkan.rosette.model.Group;
import se.ryttargardskyrkan.rosette.model.GroupMembership;
import se.ryttargardskyrkan.rosette.model.User;
import se.ryttargardskyrkan.rosette.service.GroupService;
import se.ryttargardskyrkan.rosette.service.UserService;

/**
 * Created by IntelliJ IDEA. User: durandt Date: 2011-09-23
 * <p/>
 * Class implementing the RealM for Shiro
 */
@Service("mongoRealm")
public class MongoRealm extends AuthorizingRealm {

	private UserService userService;
	private GroupService groupService;

	@Autowired
	public MongoRealm(UserService userService, GroupService groupService) {
		this.userService = userService;
		this.groupService = groupService;

	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		User user = userService.findUserById((String)principalCollection.getPrimaryPrincipal());
		
		Set<String> permissions = new HashSet<String>();
		if (user != null) {
			for (GroupMembership groupMembership : user.getGroupMemberships()) {
				Group group = groupService.findGroupById(groupMembership.getGroupId());
				permissions.addAll(group.getPermissions());
			}
		} else {
			Group group = groupService.findGroupByName("public");
			permissions.addAll(group.getPermissions());
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

			final User user = userService.findUserByUsername(providedUsername);
			if (user != null && "active".equals(user.getStatus())) {
				simpleAuthenticationInfo = new SimpleAuthenticationInfo(user.getId(), user.getHashedPassword(), "mongoRealm");
			}

			return simpleAuthenticationInfo;
		} else {
			throw new AuthenticationException("Unexpected '" + authenticationToken.getClass().getName() + "'. Expected UsernamePasswordToken instead.");
		}
	}
	
	@Override
	public CredentialsMatcher getCredentialsMatcher() {
		return new PasswordMatcher();
	}
}
