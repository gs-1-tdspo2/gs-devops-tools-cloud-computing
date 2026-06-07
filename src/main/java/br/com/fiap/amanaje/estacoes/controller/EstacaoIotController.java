package br.com.fiap.amanaje.estacoes.controller;

import br.com.fiap.amanaje.estacoes.service.EstacaoIotService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import br.com.fiap.amanaje.estacoes.dto.EstacaoCreateRequest;
import br.com.fiap.amanaje.estacoes.dto.EstacaoResponse;
import br.com.fiap.amanaje.estacoes.dto.EstacaoUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estacoes")
@Tag(name = "Estações IoT", description = "Cadastro e consulta de estações IoT")
public class EstacaoIotController {

	private final EstacaoIotService estacaoService;

	public EstacaoIotController(EstacaoIotService estacaoService) {
		this.estacaoService = estacaoService;
	}

	@PostMapping
	@Operation(summary = "Cadastrar estação IoT")
	public ResponseEntity<EstacaoResponse> criar(@Valid @RequestBody EstacaoCreateRequest request) {
		EstacaoResponse response = estacaoService.criar(request);
		return ResponseEntity
				.created(linkTo(methodOn(EstacaoIotController.class).buscarPorId(response.idEstacao())).toUri())
				.body(response);
	}

	@GetMapping("/regiao/{idRegiao}")
	@Operation(summary = "Listar estações IoT ativas por região")
	public List<EstacaoResponse> listarPorRegiao(@PathVariable Long idRegiao) {
		return estacaoService.listarAtivasPorRegiao(idRegiao);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar estação IoT ativa por ID")
	public EntityModel<EstacaoResponse> buscarPorId(@PathVariable Long id) {
		EstacaoResponse response = estacaoService.buscarPorId(id);
		return EntityModel.of(
				response,
				linkTo(methodOn(EstacaoIotController.class).buscarPorId(id)).withSelfRel(),
				Link.of("/api/leituras").withRel("leiturasPost"));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualizar estação IoT ativa")
	public EstacaoResponse atualizar(
			@PathVariable Long id,
			@Valid @RequestBody EstacaoUpdateRequest request) {
		return estacaoService.atualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Inativar estação IoT")
	public ResponseEntity<Void> inativar(@PathVariable Long id) {
		estacaoService.inativar(id);
		return ResponseEntity.noContent().build();
	}

}
