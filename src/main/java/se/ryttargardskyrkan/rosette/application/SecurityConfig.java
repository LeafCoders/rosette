package se.ryttargardskyrkan.rosette.application;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.ryttargardskyrkan.rosette.filter.OptionalBasicHttpAuthenticationFilter;
import se.ryttargardskyrkan.rosette.security.MongoRealm;

@Configuration
public class SecurityConfig {

    @Bean
    public EhCacheManager ehCacheManager() {
        return new EhCacheManager();
    }

    @Bean
    @Autowired
    public DefaultWebSecurityManager securityManager(MongoRealm mongoRealm) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(mongoRealm);
        defaultWebSecurityManager.setCacheManager(ehCacheManager());
        return defaultWebSecurityManager;
    }

    @Bean
    @Autowired
    public ShiroFilterFactoryBean shiroFilter(MongoRealm mongoRealm) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager(mongoRealm));
        shiroFilterFactoryBean.setFilterChainDefinitions("** = noSessionCreation, optionalAuthcBasic");
        return shiroFilterFactoryBean;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public OptionalBasicHttpAuthenticationFilter optionalAuthcBasic() {
        return new OptionalBasicHttpAuthenticationFilter();
    }
}
