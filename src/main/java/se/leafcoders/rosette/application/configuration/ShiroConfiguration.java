package se.leafcoders.rosette.application.configuration;
/*
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
*/
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

//import se.leafcoders.rosette.filter.OptionalBasicHttpAuthenticationFilter;
import se.leafcoders.rosette.security.MongoRealm;
import se.leafcoders.rosette.security.RosettePasswordMatcher;
import se.leafcoders.rosette.security.RosettePasswordService;

/**
 * TODO add description
 * 
 * https://github.com/pires/spring-boot-shiro-orientdb
 * 
 */
//@Configuration
public class ShiroConfiguration {
/*
	@Bean
	public ShiroFilterFactoryBean shiroFilter() {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager());
		
		// noSessionCreation will prevent session to be created. See http://shiro.apache.org/web.html#Web-DefaultFilters
		// optionalAuthcBasic will allow anonymous access. Will create an AnonymousToken as authentication token.
        shiroFilterFactoryBean.setFilterChainDefinitions("/** = noSessionCreation, optionalAuthcBasic");
		return shiroFilterFactoryBean;
	}

//	@Bean(name = "securityManager")
	public DefaultWebSecurityManager securityManager() {
		final DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(mongoRealm());

		securityManager.setCacheManager(ehCacheManager());

//		securityManager.setSessionManager(sessionManager());
		return securityManager;
	}

    @Bean
    public EhCacheManager ehCacheManager() {
        return new EhCacheManager();
    }

    @Bean
    public OptionalBasicHttpAuthenticationFilter optionalAuthcBasic() {
        return new OptionalBasicHttpAuthenticationFilter();
    }
*/

/*
	@Bean
	public DefaultWebSessionManager sessionManager() {
		final DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setSessionDAO(sessionDao());
		sessionManager.setGlobalSessionTimeout(43200000); // 12 hours
		return sessionManager;
	}
*/	

/*
	@Bean
	public SessionDAO sessionDao() {
		return new HazelcastSessionDao();
	}
*/
    
/*    
	@Bean(name = "mongoRealm")
	@DependsOn("lifecycleBeanPostProcessor")
	public MongoRealm mongoRealm() {
		final MongoRealm mongoRealm = new MongoRealm();
		mongoRealm.setAuthenticationCachingEnabled(true);
		mongoRealm.setCredentialsMatcher(credentialsMatcher());
		return mongoRealm;
	}

	@Bean(name = "credentialsMatcher")
	public PasswordMatcher credentialsMatcher() {
		final PasswordMatcher credentialsMatcher = new RosettePasswordMatcher();
		credentialsMatcher.setPasswordService(passwordService());
		return credentialsMatcher;
	}

	@Bean(name = "passwordService")
	public DefaultPasswordService passwordService() {
		return new RosettePasswordService();
	}

	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}
*/	
}