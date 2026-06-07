package br.com.fiap.amanaje.leituras.controller;

import br.com.fiap.amanaje.leituras.config.LeituraHttpProperties;
import br.com.fiap.amanaje.leituras.service.LeituraIotService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import br.com.fiap.amanaje.leituras.dto.LeituraIotCreateRequest;
import br.com.fiap.amanaje.leituras.dto.LeituraIotResponse;
import br.com.fiap.amanaje.riscos.service.RiscoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Leituras IoT", description = "Recebimento HTTP e histórico de telemetria das estações IoT")
public class LeituraIotController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LeituraIotController.class);

	private final LeituraIotService leituraService;
	private final RiscoService riscoService;
	private final LeituraHttpProperties httpProperties;

	public LeituraIotController(
			LeituraIotService leituraService,
			RiscoService riscoService,
			LeituraHttpProperties httpProperties) {
		this.leituraService = leituraService;
		this.riscoService = riscoService;
		this.httpProperties = httpProperties;
	}

	@PostMapping("/leituras")
	@Operation(summary = "Receber leitura de telemetria por HTTP")
	public ResponseEntity<LeituraIotResponse> criar(@Valid @RequestBody LeituraIotCreateRequest request) {
		LeituraIotResponse response = leituraService.criar(request);
		avaliarRiscoHttpSeHabilitado(response);
		return ResponseEntity
				.created(linkTo(methodOn(LeituraIotController.class).listarPorRegiao(response.idRegiao())).toUri())
				.body(response);
	}

	@GetMapping("/regioes/{id}/leituras")
	@Operation(summary = "Listar histórico de leituras por região")
	public List<LeituraIotResponse> listarPorRegiao(@PathVariable Long id) {
		return leituraService.listarPorRegiao(id);
	}

	private void avaliarRiscoHttpSeHabilitado(LeituraIotResponse leitura) {
		if (!httpProperties.isEvaluateRisk()) {
			LOGGER.info("Avaliação de risco HTTP desabilitada para idLeitura={} idRegiao={}",
					leitura.idLeitura(),
					leitura.idRegiao());
			return;
		}

		try {
			riscoService.avaliar(leitura.idRegiao());
			LOGGER.info("Avaliação de risco HTTP executada para idLeitura={} idRegiao={}",
					leitura.idLeitura(),
					leitura.idRegiao());
		}
		catch (RuntimeException ex) {
			LOGGER.error("Leitura HTTP salva, mas avaliação de risco falhou para idLeitura={} idRegiao={}: {}",
					leitura.idLeitura(),
					leitura.idRegiao(),
					ex.getMessage(),
					ex);
		}
	}

}
