package br.com.fiap.amanaje.usuarios.service;

import br.com.fiap.amanaje.usuarios.enums.StatusUsuario;

import br.com.fiap.amanaje.usuarios.repository.UsuarioRepository;

import br.com.fiap.amanaje.usuarios.model.Usuario;

import br.com.fiap.amanaje.clientes.service.ClienteService;

import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.amanaje.clientes.service.ClienteService;
import br.com.fiap.amanaje.common.exception.BusinessRuleException;
import br.com.fiap.amanaje.common.exception.ResourceNotFoundException;
import br.com.fiap.amanaje.usuarios.dto.UsuarioCreateRequest;
import br.com.fiap.amanaje.usuarios.dto.UsuarioResponse;
import br.com.fiap.amanaje.usuarios.dto.UsuarioUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

	private static final String ATIVO = "S";
	private static final String INATIVO = "N";

	private final UsuarioRepository usuarioRepository;
	private final ClienteService clienteService;

	public UsuarioService(UsuarioRepository usuarioRepository, ClienteService clienteService) {
		this.usuarioRepository = usuarioRepository;
		this.clienteService = clienteService;
	}

	@Transactional
	public UsuarioResponse criar(UsuarioCreateRequest request) {
		clienteService.buscarAtivo(request.idCliente());
		validarEmailUnico(request.email(), null);
		Usuario usuario = Usuario.builder()
				.idCliente(request.idCliente())
				.nome(request.nome())
				.email(request.email())
				.senhaHash(request.senhaHash())
				.perfil(request.perfil())
				.statusUsuario(request.status())
				.stAtivo(ATIVO)
				.dtCriadoEm(LocalDateTime.now())
				.build();

		return toResponse(usuarioRepository.save(usuario));
	}

	@Transactional(readOnly = true)
	public List<UsuarioResponse> listarAtivos(Long idCliente) {
		List<Usuario> usuarios;
		if (idCliente == null) {
			usuarios = usuarioRepository.findByStAtivo(ATIVO);
		} else {
			clienteService.buscarAtivo(idCliente);
			usuarios = usuarioRepository.findByIdClienteAndStAtivo(idCliente, ATIVO);
		}
		return usuarios.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public UsuarioResponse buscarPorId(Long idUsuario) {
		return toResponse(buscarAtivo(idUsuario));
	}

	@Transactional
	public UsuarioResponse atualizar(Long idUsuario, UsuarioUpdateRequest request) {
		Usuario usuario = buscarAtivo(idUsuario);
		validarEmailUnico(request.email(), idUsuario);
		usuario.setNome(request.nome());
		usuario.setEmail(request.email());
		usuario.setSenhaHash(request.senhaHash());
		usuario.setPerfil(request.perfil());
		usuario.setStatusUsuario(request.status());
		usuario.setDtAtualizadoEm(LocalDateTime.now());

		return toResponse(usuarioRepository.save(usuario));
	}

	@Transactional
	public void inativar(Long idUsuario) {
		Usuario usuario = buscarAtivo(idUsuario);
		LocalDateTime agora = LocalDateTime.now();
		usuario.setStAtivo(INATIVO);
		usuario.setDtDelEm(agora);
		usuario.setDtAtualizadoEm(agora);
		usuarioRepository.save(usuario);
	}

	@Transactional(readOnly = true)
	public Usuario buscarAtivo(Long idUsuario) {
		return usuarioRepository.findById(idUsuario)
				.filter(usuario -> ATIVO.equals(usuario.getStAtivo()))
				.orElseThrow(() -> new ResourceNotFoundException("Usuário ativo não encontrado: " + idUsuario));
	}

	private void validarEmailUnico(String email, Long idUsuarioAtual) {
		usuarioRepository.findByEmail(email)
				.filter(usuario -> !usuario.getIdUsuario().equals(idUsuarioAtual))
				.ifPresent(usuario -> {
					throw new BusinessRuleException("E-mail de usuário já cadastrado: " + email);
				});
	}

	private UsuarioResponse toResponse(Usuario usuario) {
		return new UsuarioResponse(
				usuario.getIdUsuario(),
				usuario.getIdCliente(),
				usuario.getNome(),
				usuario.getEmail(),
				usuario.getPerfil(),
				usuario.getStatusUsuario(),
				usuario.getStAtivo(),
				usuario.getDtCriadoEm(),
				usuario.getDtAtualizadoEm());
	}

}
