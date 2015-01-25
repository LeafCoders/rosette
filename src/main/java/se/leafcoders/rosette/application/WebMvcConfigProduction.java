package se.leafcoders.rosette.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Profile("production")
@Configuration
@ComponentScan(basePackages = {"se.leafcoders.rosette"})
@EnableWebMvc
@EnableAspectJAutoProxy
@PropertySource("classpath:/settings.properties")
class WebMvcConfigProduction extends WebMvcConfigDefault {

    @Bean
    public ApplicationSettings applicationSettings() {
    	return new ApplicationSettings()
    				.enableUploadCacheMaxAge()
					.lock();
    }
}
