package br.com.fiap.amanaje.observacoes.controller;

import br.com.fiap.amanaje.observacoes.service.ObservacaoClimaticaService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import br.com.fiap.amanaje.observacoes.dto.ObservacaoClimaticaCreateRequest;
import br.com.fiap.amanaje.observacoes.dto.ObservacaoClimaticaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Observações Climáticas", description = "Persistência de dados climáticos recebidos por integração externa")
public class ObservacaoClimaticaController {

	private final ObservacaoClimaticaService observacaoService;

	public ObservacaoClimaticaController(ObservacaoClimaticaService observacaoService) {
		this.observacaoService = observacaoService;
	}

	@PostMapping("/observacoes-climaticas")
	@Operation(summary = "Persistir observação climática externa")
	public ResponseEntity<ObservacaoClimaticaResponse> criar(
			@Valid @RequestBody ObservacaoClimaticaCreateRequest request) {
		ObservacaoClimaticaResponse response = observacaoService.criar(request);
		return ResponseEntity
				.created(linkTo(methodOn(ObservacaoClimaticaController.class)
						.buscarUltimaPorRegiao(response.idRegiao())).toUri())
				.body(response);
	}

	@GetMapping("/regioes/{id}/observacoes-climaticas/ultima")
	@Operation(summary = "Buscar última observação climática por região")
	public ObservacaoClimaticaResponse buscarUltimaPorRegiao(@PathVariable Long id) {
		return observacaoService.buscarUltimaPorRegiao(id);
	}

}
