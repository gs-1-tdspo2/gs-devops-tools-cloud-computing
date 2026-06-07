package br.com.fiap.amanaje.usuarios.controller;

import br.com.fiap.amanaje.usuarios.service.UsuarioService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import br.com.fiap.amanaje.usuarios.dto.UsuarioCreateRequest;
import br.com.fiap.amanaje.usuarios.dto.UsuarioResponse;
import br.com.fiap.amanaje.usuarios.dto.UsuarioUpdateRequest;
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
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Administração básica de usuários vinculados a clientes")
public class UsuarioController {

	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@PostMapping
	@Operation(summary = "Cadastrar usuário")
	public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioCreateRequest request) {
		UsuarioResponse response = usuarioService.criar(request);
		return ResponseEntity
				.created(linkTo(methodOn(UsuarioController.class).buscarPorId(response.idUsuario())).toUri())
				.body(response);
	}

	@GetMapping
	@Operation(summary = "Listar usuários ativos")
	public List<UsuarioResponse> listar(@RequestParam(required = false) Long idCliente) {
		return usuarioService.listarAtivos(idCliente);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar usuário ativo por ID")
	public UsuarioResponse buscarPorId(@PathVariable Long id) {
		return usuarioService.buscarPorId(id);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualizar usuário ativo")
	public UsuarioResponse atualizar(
			@PathVariable Long id,
			@Valid @RequestBody UsuarioUpdateRequest request) {
		return usuarioService.atualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Inativar usuário")
	public ResponseEntity<Void> inativar(@PathVariable Long id) {
		usuarioService.inativar(id);
		return ResponseEntity.noContent().build();
	}

}
