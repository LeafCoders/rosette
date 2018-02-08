package se.leafcoders.rosette;

import java.util.Arrays;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/*
 * To start mysql
 * docker run --detach --name=mysql --env="MYSQL_ROOT_PASSWORD=root" --publish 3306:3306 mysql
 */

@SpringBootApplication
@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
@ComponentScan(basePackages = {
	"se.leafcoders.rosette",
	"se.leafcoders.rosette.persitency.repository",
	"se.leafcoders.rosette.controller",
	"se.leafcoders.rosette.auth",
	"se.leafcoders.rosette.auth.jwt",
})
@EnableScheduling
public class RosetteApplication extends SpringBootServletInitializer { // Is SpringBootServletInitializer needed?

    public static void main(String[] args) {
        SpringApplication.run(RosetteApplication.class, args);
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
        config.setAllowedMethods(Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
    
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RosetteApplication.class).bannerMode(Banner.Mode.OFF);
    }
}
