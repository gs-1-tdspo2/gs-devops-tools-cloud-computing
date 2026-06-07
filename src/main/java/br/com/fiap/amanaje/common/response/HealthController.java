package br.com.fiap.amanaje.common.response;

import java.time.Instant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Saúde da Aplicação", description = "Verificação simples de disponibilidade da API")
public class HealthController {

	@GetMapping("/health")
	@Operation(summary = "Consultar disponibilidade da API")
	public HealthResponse health() {
		return new HealthResponse(
				"Amanajé API",
				"UP",
				"API principal do Amanajé em execução",
				Instant.now());
	}

	public record HealthResponse(String application, String status, String message, Instant timestamp) {
	}

}
