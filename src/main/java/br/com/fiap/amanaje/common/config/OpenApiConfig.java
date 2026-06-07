package br.com.fiap.amanaje.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI amanajeOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Amanajé API")
						.description("API principal do projeto Amanajé Global Solution 2026/1 para clientes, "
								+ "regiões monitoradas, estações IoT, leituras, avaliação de risco, alertas e dashboard.")
						.version("v1"));
	}

}
