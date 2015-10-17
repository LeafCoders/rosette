package se.leafcoders.rosette.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import se.leafcoders.rosette.auth.CurrentUserService;
import se.leafcoders.rosette.auth.jwt.JwtAuthenticationFilter;
import se.leafcoders.rosette.auth.jwt.JwtAuthenticationService;
import se.leafcoders.rosette.auth.jwt.JwtLoginFilter;

@Configuration
@EnableWebSecurity
@Order(2)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CurrentUserService userDetailsService;
    private final JwtAuthenticationService tokenAuthenticationService;

    public SpringSecurityConfig() {
        // Disable default Spring security configuration
        super(true);

        this.userDetailsService = new CurrentUserService();
        tokenAuthenticationService = new JwtAuthenticationService("tooManySecrets", userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // TODO: Describe why!
                .exceptionHandling().and()

                // Anonymous users will be represented with an org.springframework.security.authentication.AnonymousAuthenticationToken
                .anonymous().and()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                
                // TODO: Describe why!
                .servletApi().and()
                // TODO: Use this??? .securityContext().and()

                // TODO: Describe why!
                .headers().cacheControl().and().and()

                .authorizeRequests()
                    // Allow anonymous logins
                    .antMatchers(HttpMethod.POST, "/auth/**").permitAll()
                    // Allow anonymous sign up
                    .antMatchers(HttpMethod.POST, "/api/v1/signupUsers").permitAll()
                    // Allow anonymous public resource requests
                    .antMatchers(HttpMethod.GET, "/api/v1/public/**").permitAll()
                    // All other request need to be authenticated
                    .anyRequest().authenticated().and()

                 // Custom authentication which sets the token header upon authentication
                .addFilterBefore(new JwtLoginFilter("/auth/login", tokenAuthenticationService, authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                    
                // Custom Token based authentication with header value
                // previously given to the client
                .addFilterBefore(new JwtAuthenticationFilter(tokenAuthenticationService), AnonymousAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(new BCryptPasswordEncoder());
        auth.eraseCredentials(true);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Bean
    public JwtAuthenticationService tokenAuthenticationService() {
        return tokenAuthenticationService;
    }

}
