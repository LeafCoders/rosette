package se.leafcoders.rosette.application;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Profile("!production")
@Configuration
@ComponentScan(basePackages = {"se.leafcoders.rosette"})
@EnableWebMvc
@PropertySource("classpath:/settings.properties")
class WebMvcConfigDefault extends WebMvcConfigurerAdapter {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
       return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean
    public ApplicationSettings applicationSettings() {
    	return new ApplicationSettings().lock();
    }
}
