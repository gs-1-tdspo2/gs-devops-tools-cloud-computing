package br.com.fiap.amanaje.clientes.controller;

import br.com.fiap.amanaje.regioes.controller.RegiaoMonitoradaController;

import br.com.fiap.amanaje.clientes.service.ClienteService;

import br.com.fiap.amanaje.clientes.model.Cliente;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import br.com.fiap.amanaje.clientes.dto.ClienteCreateRequest;
import br.com.fiap.amanaje.clientes.dto.ClienteResponse;
import br.com.fiap.amanaje.clientes.dto.ClienteUpdateRequest;
import br.com.fiap.amanaje.regioes.controller.RegiaoMonitoradaController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
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
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Cadastro de clientes institucionais e privados")
public class ClienteController {

	private final ClienteService clienteService;

	public ClienteController(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	@PostMapping
	@Operation(summary = "Cadastrar cliente")
	public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteCreateRequest request) {
		ClienteResponse response = clienteService.criar(request);
		return ResponseEntity
				.created(linkTo(methodOn(ClienteController.class).buscarPorId(response.idCliente())).toUri())
				.body(response);
	}

	@GetMapping
	@Operation(summary = "Listar clientes ativos")
	public List<ClienteResponse> listar() {
		return clienteService.listarAtivos();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar cliente ativo por ID")
	public EntityModel<ClienteResponse> buscarPorId(@PathVariable Long id) {
		ClienteResponse response = clienteService.buscarPorId(id);
		return EntityModel.of(
				response,
				linkTo(methodOn(ClienteController.class).buscarPorId(id)).withSelfRel(),
				linkTo(methodOn(RegiaoMonitoradaController.class).listar(id, null, null, null)).withRel("regioes"));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualizar cliente ativo")
	public ClienteResponse atualizar(
			@PathVariable Long id,
			@Valid @RequestBody ClienteUpdateRequest request) {
		return clienteService.atualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Inativar cliente")
	public ResponseEntity<Void> inativar(@PathVariable Long id) {
		clienteService.inativar(id);
		return ResponseEntity.noContent().build();
	}

}
