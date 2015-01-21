package se.leafcoders.rosette.application;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import se.leafcoders.rosette.aspect.MethodLogger;
import se.leafcoders.rosette.filter.JsonpCallbackFilter;

@Configuration
@ComponentScan(basePackages = {"se.leafcoders.rosette"})
@EnableWebMvc
@EnableAspectJAutoProxy
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
