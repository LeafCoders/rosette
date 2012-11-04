package se.ryttargardskyrkan.rosette.security;

import groovyjarjarasm.asm.commons.AnalyzerAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import se.ryttargardskyrkan.rosette.model.Group;
import se.ryttargardskyrkan.rosette.model.GroupMembership;
import se.ryttargardskyrkan.rosette.model.User;
import se.ryttargardskyrkan.rosette.service.UserService;

/**
 * Created by IntelliJ IDEA. User: durandt Date: 2011-09-23
 * <p/>
 * Class implementing the RealM for Shiro
 */
@Service("mongoRealm")
public class MongoRealm extends AuthorizingRealm {

	private UserService userService;
	private MongoTemplate mongoTemplate;

	@Autowired
	public MongoRealm(UserService userService, MongoTemplate mongoTemplate) {
		this.userService = userService;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		Set<String> permissions = new HashSet<String>();

		if (!principalCollection.fromRealm("anonymousRealm").isEmpty()) {
			permissions.add("*:read");
		} else {
			User user = userService.findUserById((String) principalCollection.getPrimaryPrincipal());
			Query groupMembershipsQuery = new Query(Criteria.where("userId").is(user.getId()));
			List<GroupMembership> groupMemberships = mongoTemplate.find(groupMembershipsQuery, GroupMembership.class);

			if (groupMemberships != null) {
				List<String> groupIds = new ArrayList<String>();
				for (GroupMembership groupMembership : groupMemberships) {
					groupIds.add(groupMembership.getGroupId());
				}

				if (!groupIds.isEmpty()) {
					Query groupQuery = new Query(Criteria.where("id").in(groupIds));
					groupQuery.fields().include("permissions");
					List<Group> groups = mongoTemplate.find(groupQuery, Group.class);

					if (groups != null) {
						for (Group group : groups) {
							if (group.getPermissions() != null) {
								permissions.addAll(group.getPermissions());
							}
						}
					}
				}
			}
		}

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
		} else if (authenticationToken instanceof AnonymousToken) {
			return new SimpleAuthenticationInfo("", "", "anonymousRealm");
		} else {
			throw new AuthenticationException("Unexpected '" + authenticationToken.getClass().getName() + "'. Expected UsernamePasswordToken instead.");
		}
	}

	@Override
	public CredentialsMatcher getCredentialsMatcher() {
		return new PasswordMatcher();
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
}
