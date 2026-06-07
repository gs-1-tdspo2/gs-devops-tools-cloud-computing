package br.com.fiap.amanaje.regioes.controller;

import br.com.fiap.amanaje.regioes.enums.TipoVisibilidade;

import br.com.fiap.amanaje.regioes.service.RegiaoMonitoradaService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.List;

import br.com.fiap.amanaje.regioes.dto.RegiaoCreateRequest;
import br.com.fiap.amanaje.regioes.dto.RegiaoResponse;
import br.com.fiap.amanaje.regioes.dto.RegiaoUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/regioes")
@Tag(name = "Regiões Monitoradas", description = "Cadastro e consulta de áreas monitoradas")
public class RegiaoMonitoradaController {

	private final RegiaoMonitoradaService regiaoService;

	public RegiaoMonitoradaController(RegiaoMonitoradaService regiaoService) {
		this.regiaoService = regiaoService;
	}

	@PostMapping
	@Operation(summary = "Cadastrar região monitorada")
	public ResponseEntity<RegiaoResponse> criar(@Valid @RequestBody RegiaoCreateRequest request) {
		RegiaoResponse response = regiaoService.criar(request);
		return ResponseEntity
				.created(linkTo(RegiaoMonitoradaController.class).slash(response.idRegiao()).toUri())
				.body(response);
	}

	@GetMapping
	@Operation(summary = "Listar regiões monitoradas ativas")
	public List<RegiaoResponse> listar(
			@RequestParam(required = false) Long idCliente,
			@RequestParam(required = false) String estado,
			@RequestParam(required = false) String cidade,
			@RequestParam(name = "visibilidade", required = false) TipoVisibilidade tipoVisibilidade) {
		return regiaoService.listarAtivas(idCliente, estado, cidade, tipoVisibilidade);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar região monitorada ativa por ID")
	public RegiaoResponse buscarPorId(@PathVariable Long id) {
		return regiaoService.buscarPorId(id);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualizar região monitorada ativa")
	public RegiaoResponse atualizar(
			@PathVariable Long id,
			@Valid @RequestBody RegiaoUpdateRequest request) {
		return regiaoService.atualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Inativar região monitorada")
	public ResponseEntity<Void> inativar(@PathVariable Long id) {
		regiaoService.inativar(id);
		return ResponseEntity.noContent().build();
	}

}
