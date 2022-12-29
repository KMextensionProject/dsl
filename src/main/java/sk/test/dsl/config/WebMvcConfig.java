package sk.test.dsl.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebMvcConfig extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] {
			AppConfig.class
		};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return null; // NOSONAR
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

}
