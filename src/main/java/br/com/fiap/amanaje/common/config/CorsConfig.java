package br.com.fiap.amanaje.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	private final AmanajeCorsProperties corsProperties;

	public CorsConfig(AmanajeCorsProperties corsProperties) {
		this.corsProperties = corsProperties;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOrigins(corsProperties.getAllowedOrigins().toArray(String[]::new))
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.exposedHeaders("Location")
				.allowCredentials(false)
				.maxAge(3600);
	}

}
