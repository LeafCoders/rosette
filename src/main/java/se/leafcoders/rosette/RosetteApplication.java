package se.leafcoders.rosette;

import java.util.Arrays;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
@Configuration
@EnableJpaRepositories
@PersistenceContext(type = PersistenceContextType.TRANSACTION)
@EnableTransactionManagement
@ComponentScan
@EnableScheduling
public class RosetteApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(RosetteApplication.class, args);
    }

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Enable JSR-303 validation
     */
    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    /**
     * Enable CORS for whole application
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addExposedHeader("X-AUTH-TOKEN");
        config.applyPermitDefaultValues();
        config.setAllowedMethods(Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Profile("development")
    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        return filter;
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RosetteApplication.class).bannerMode(Banner.Mode.OFF);
    }
}
