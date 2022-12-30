package sk.test.dsl.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
public class WebAppInitializer implements WebMvcConfigurer {

	// so the string values from @PathVariables and @RequestParameters can be translated into Enums
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new StringToStoreNameConverter());
		registry.addConverter(new StringToCategoryConverter());
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(0, getJackson2HttpMessageConverter());
	}

	@Bean
	public MappingJackson2HttpMessageConverter getJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter jackson2HttpMsgConverter = new MappingJackson2HttpMessageConverter();
		jackson2HttpMsgConverter.setObjectMapper(getObjectMapper());
		return jackson2HttpMsgConverter;
	}

	@Bean
	public ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		return objectMapper;
	}
}
