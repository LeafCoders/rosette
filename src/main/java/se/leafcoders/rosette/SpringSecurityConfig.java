package se.leafcoders.rosette;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
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
import se.leafcoders.rosette.auth.CurrentUserService;
import se.leafcoders.rosette.auth.RosetteAuthority;
import se.leafcoders.rosette.auth.RosetteAnonymousAuthenticationFilter;
import se.leafcoders.rosette.auth.jwt.JwtAuthenticationFilter;
import se.leafcoders.rosette.auth.jwt.JwtAuthenticationService;

@Configuration
@EnableWebSecurity
@Order(2)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RosetteSettings rosetteSettings;

    private final CurrentUserService userDetailsService;
    private JwtAuthenticationService tokenAuthenticationService = null;

    public SpringSecurityConfig() {
        // Disable default Spring security configuration
        super(true);

        this.userDetailsService = new CurrentUserService();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // TODO: Describe why!
                .exceptionHandling().and()

                // Anonymous users will be represented with an
                // org.springframework.security.authentication.AnonymousAuthenticationToken
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
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasAnyAuthority(RosetteAuthority.SUPER_ADMIN)
                // All other request need to be authenticated
                .anyRequest().authenticated().and()

                // Anonymous authentication will be added to requests without valid JWT token
                .addFilterBefore(new RosetteAnonymousAuthenticationFilter(), AnonymousAuthenticationFilter.class)

                // Existing user authentication will be added to requests with valid JWT token
                .addFilterBefore(new JwtAuthenticationFilter(tokenAuthenticationService()),
                        RosetteAnonymousAuthenticationFilter.class);
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
        if (tokenAuthenticationService == null) {
            try {
                tokenAuthenticationService = new JwtAuthenticationService(rosetteSettings.getJwtSecretToken(),
                        userDetailsService);
            } catch (Exception exception) {
                System.err.println(exception.getMessage());
                System.exit(0);
            }
        }
        return tokenAuthenticationService;
    }

    @Bean
    public AnonymousAuthenticationFilter anonymousAuthenticationFilter() {
        AnonymousAuthenticationFilter filter = new AnonymousAuthenticationFilter("anon");
        return filter;
    }

}
