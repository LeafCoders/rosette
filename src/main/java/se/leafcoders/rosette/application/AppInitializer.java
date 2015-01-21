package se.leafcoders.rosette.application;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Set;

public class AppInitializer implements WebApplicationInitializer {

	private static final String MAPPING_URL = "/api/*";

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		// Register web mvc config
		AnnotationConfigWebApplicationContext webMvcContext = new AnnotationConfigWebApplicationContext();
        webMvcContext.scan("se.leafcoders.rosette.application");
		servletContext.addListener(new ContextLoaderListener(webMvcContext));

        // Setup filters
		FilterRegistration.Dynamic ef = servletContext.addFilter("encodingFilter", new CharacterEncodingFilter());
		ef.setInitParameter("encoding", "UTF-8");
		ef.setInitParameter("forceEncoding", "true");
		ef.addMappingForUrlPatterns(null, true, MAPPING_URL);

		FilterRegistration.Dynamic sf = servletContext.addFilter("shiroFilter", new DelegatingFilterProxy());
		sf.setInitParameter("targetFilterLifecycle", "true");
		sf.addMappingForUrlPatterns(null, true, MAPPING_URL);

		FilterRegistration.Dynamic jpf = servletContext.addFilter("jsonpFilter", new DelegatingFilterProxy());
		jpf.addMappingForUrlPatterns(null, true, MAPPING_URL);

		// The main Spring MVC servlet
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(new GenericWebApplicationContext()));
		dispatcher.setLoadOnStartup(1);
		Set<String> mappingConflicts = dispatcher.addMapping(MAPPING_URL);

		// Check mapping conflicts
		if (!mappingConflicts.isEmpty()) {
			for (String s : mappingConflicts) {
				System.err.print("Mapping conflict: " + s);
			}
			throw new IllegalStateException("'appServlet' cannot be mapped to '/' under Tomcat versions <= 7.0.14");
		}
	}
}
