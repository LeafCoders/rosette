package se.ryttargardskyrkan.rosette.security;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: durandt
 * Date: 2011-09-23
 * <p/>
 * Class implementing the RealM for Shiro
 */
@Service("mongoRealm")
public class MongoRealm extends AuthorizingRealm {

//    private UserService userService;
//    private AuthService authService;
//
//    @Autowired
//    public MongoRealm(UserService userService, AuthService authService) {
//        this.userService = userService;
//        this.authService = authService;
//    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
//        String id = (String) principalCollection.getPrimaryPrincipal();
//        User user = userService.findUserById(id);
//
//        Set<String> permissions = authService.findPermissions(user.getRoleIds());
    	Set<String> permissions = new HashSet<String>();
    	permissions.add("events:post");
    	permissions.add("events:get");

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permissions);
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws
                                                                                                  AuthenticationException {
//        if (authenticationToken instanceof UsernamePasswordToken) {
//            UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
//            String providedUsername = token.getUsername();
//
//            final User user = userService.findUserByEmail(providedUsername);
//            if (user != null && User.Status.CONFIRMED.equals(user.getStatus())) {
//                return new SimpleAuthenticationInfo(user.getId(), user.getHashedPassword(), "mongoRealm");
                return new SimpleAuthenticationInfo("test", "test", "mongoRealm");
//            }
//
//            return null;
//        } else {
//            throw new AuthenticationException("Unexpected '" + authenticationToken.getClass()
//                                                                                  .getName() + "'. Expected UsernamePasswordToken instead.");
//        }
    }
}
