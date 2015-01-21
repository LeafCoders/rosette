package se.leafcoders.rosette.application;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.leafcoders.rosette.filter.OptionalBasicHttpAuthenticationFilter;
import se.leafcoders.rosette.security.MongoRealm;

@Configuration
public class SecurityConfig {
    @Bean
    @Autowired
    public ShiroFilterFactoryBean shiroFilter(MongoRealm mongoRealm) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager(mongoRealm));
        shiroFilterFactoryBean.setFilterChainDefinitions("/** = noSessionCreation, optionalAuthcBasic");
        return shiroFilterFactoryBean;
    }

    @Bean
    @Autowired
    public DefaultWebSecurityManager securityManager(MongoRealm mongoRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(mongoRealm);
        securityManager.setCacheManager(ehCacheManager());
        return securityManager;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public EhCacheManager ehCacheManager() {
        return new EhCacheManager();
    }

    @Bean
    public OptionalBasicHttpAuthenticationFilter optionalAuthcBasic() {
        return new OptionalBasicHttpAuthenticationFilter();
    }
}
