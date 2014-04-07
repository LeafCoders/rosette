package se.ryttargardskyrkan.rosette.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import se.ryttargardskyrkan.rosette.aspect.MethodLogger;
import se.ryttargardskyrkan.rosette.filter.JsonpCallbackFilter;

@Configuration
@ComponentScan(basePackages = {"se.ryttargardskyrkan.rosette.controller", "se.ryttargardskyrkan.rosette.service"})
@EnableWebMvc
@EnableAspectJAutoProxy
@ImportResource({"/WEB-INF/spring/persistence.xml", "/WEB-INF/spring/security.xml", "/WEB-INF/spring/validation.xml"})
@PropertySource("classpath:/settings.properties")
class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Bean
	public MethodLogger methodLogger() {
		return new MethodLogger();
	}

	@Bean
	public JsonpCallbackFilter jsonpFilter() {
		return new JsonpCallbackFilter();
	}

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
       return new PropertySourcesPlaceholderConfigurer();
    }
}
