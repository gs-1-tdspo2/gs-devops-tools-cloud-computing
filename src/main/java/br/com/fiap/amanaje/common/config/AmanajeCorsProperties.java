package br.com.fiap.amanaje.common.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "amanaje.cors")
public class AmanajeCorsProperties {

	private List<String> allowedOrigins = List.of(
			"http://localhost:8082",
			"http://127.0.0.1:8082",
			"http://localhost:19006",
			"http://127.0.0.1:19006",
			"http://localhost:3000",
			"http://127.0.0.1:3000",
			"http://localhost:5173",
			"http://127.0.0.1:5173",
			"https://amanaje-no2er0rxy-guscrevelaris-projects.vercel.app");

	public List<String> getAllowedOrigins() {
		return allowedOrigins;
	}

	public void setAllowedOrigins(List<String> allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}

}
