package se.ryttargardskyrkan.rosette.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

import se.ryttargardskyrkan.rosette.model.GroupMembership;
import se.ryttargardskyrkan.rosette.model.Permission;
import se.ryttargardskyrkan.rosette.model.User;

@Service("mongoRealm")
public class MongoRealm extends AuthorizingRealm {

	@Autowired
	private MongoTemplate mongoTemplate;

	PasswordMatcher passwordMatcher;

	@PostConstruct
	public void initialize() {
		setAuthenticationCachingEnabled(true);
		passwordMatcher = new RosettePasswordMatcher();
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		Set<String> permissions = new HashSet<String>();

		// Adding permissions for anyone
		Query query = Query.query(Criteria.where("anyone").is(true));
		Permission permission = mongoTemplate.findOne(query, Permission.class);
		if (permission != null && permission.getPatterns() != null) {
			permissions.addAll(permission.getPatterns());
		}

		if (principalCollection.fromRealm("anonymousRealm").isEmpty()) {
			// Adding group permissions
			User user = mongoTemplate.findById((String) principalCollection.getPrimaryPrincipal(), User.class);
			Query groupMembershipsQuery = new Query(Criteria.where("userId").is(user.getId()));
			List<GroupMembership> groupMemberships = mongoTemplate.find(groupMembershipsQuery, GroupMembership.class);

			if (groupMemberships != null) {
				List<String> groupIds = new ArrayList<String>();
				for (GroupMembership groupMembership : groupMemberships) {
					groupIds.add(groupMembership.getGroupId());
				}

				Query groupPermissionQuery = Query.query(Criteria.where("groupId").in(groupIds));
				List<Permission> groupPermissions = mongoTemplate.find(groupPermissionQuery, Permission.class);
				if (groupPermissions != null) {
					for (Permission groupPermission : groupPermissions) {
						if (groupPermission.getPatterns() != null) {
							permissions.addAll(groupPermission.getPatterns());
						}
					}
				}
			}

			// Adding user permissions
			Query userPermissionQuery = Query.query(Criteria.where("userId").is(user.getId()));
			List<Permission> userPermissions = mongoTemplate.find(userPermissionQuery, Permission.class);
			if (userPermissions != null) {
				for (Permission userPermission : userPermissions) {
					if (userPermission.getPatterns() != null) {
						permissions.addAll(userPermission.getPatterns());
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

			final User user = mongoTemplate.findOne(Query.query(Criteria.where("username").is(providedUsername)), User.class);
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
