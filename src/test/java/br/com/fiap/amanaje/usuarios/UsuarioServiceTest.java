package br.com.fiap.amanaje.usuarios;

import br.com.fiap.amanaje.usuarios.enums.StatusUsuario;

import br.com.fiap.amanaje.usuarios.enums.PerfilUsuario;

import br.com.fiap.amanaje.usuarios.repository.UsuarioRepository;

import br.com.fiap.amanaje.usuarios.service.UsuarioService;

import br.com.fiap.amanaje.usuarios.model.Usuario;

import br.com.fiap.amanaje.clientes.service.ClienteService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import br.com.fiap.amanaje.clientes.model.Cliente;
import br.com.fiap.amanaje.clientes.service.ClienteService;
import br.com.fiap.amanaje.common.exception.BusinessRuleException;
import br.com.fiap.amanaje.usuarios.dto.UsuarioCreateRequest;
import br.com.fiap.amanaje.usuarios.dto.UsuarioResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private ClienteService clienteService;

	@InjectMocks
	private UsuarioService usuarioService;

	@Test
	void shouldCreateUsuarioForActiveClienteWithoutReturningSenhaHash() {
		UsuarioCreateRequest request = request();
		when(clienteService.buscarAtivo(10L)).thenReturn(Cliente.builder().idCliente(10L).build());
		when(usuarioRepository.findByEmail("operador@amanaje.com.br")).thenReturn(Optional.empty());
		when(usuarioRepository.save(any(Usuario.class)))
				.thenAnswer(invocation -> {
					Usuario usuario = invocation.getArgument(0);
					usuario.setIdUsuario(20L);
					return usuario;
				});

		UsuarioResponse response = usuarioService.criar(request);

		verify(clienteService).buscarAtivo(10L);
		assertThat(response.idUsuario()).isEqualTo(20L);
		assertThat(response.idCliente()).isEqualTo(10L);
		assertThat(response.email()).isEqualTo("operador@amanaje.com.br");
		assertThat(response.stAtivo()).isEqualTo("S");
		assertThat(response.criadoEm()).isNotNull();
	}

	@Test
	void shouldRejectDuplicatedEmail() {
		UsuarioCreateRequest request = request();
		when(clienteService.buscarAtivo(10L)).thenReturn(Cliente.builder().idCliente(10L).build());
		when(usuarioRepository.findByEmail("operador@amanaje.com.br"))
				.thenReturn(Optional.of(Usuario.builder().idUsuario(30L).build()));

		assertThatThrownBy(() -> usuarioService.criar(request))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("E-mail de usuário já cadastrado: operador@amanaje.com.br");
		verify(usuarioRepository, never()).save(any(Usuario.class));
	}

	@Test
	void shouldInactivateUsuarioWithoutDeletingIt() {
		Usuario usuario = Usuario.builder()
				.idUsuario(20L)
				.stAtivo("S")
				.build();
		when(usuarioRepository.findById(20L)).thenReturn(Optional.of(usuario));

		usuarioService.inativar(20L);

		ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
		verify(usuarioRepository).save(captor.capture());
		assertThat(captor.getValue().getStAtivo()).isEqualTo("N");
		assertThat(captor.getValue().getDtDelEm()).isNotNull();
		assertThat(captor.getValue().getDtAtualizadoEm()).isNotNull();
	}

	private UsuarioCreateRequest request() {
		return new UsuarioCreateRequest(
				10L,
				"Operador Amanajé",
				"operador@amanaje.com.br",
				"hash-persistido",
				PerfilUsuario.OPERADOR,
				StatusUsuario.ATIVO);
	}

}
