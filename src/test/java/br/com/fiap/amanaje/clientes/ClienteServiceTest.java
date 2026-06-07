package br.com.fiap.amanaje.clientes;

import br.com.fiap.amanaje.clientes.repository.ClienteRepository;

import br.com.fiap.amanaje.clientes.service.ClienteService;

import br.com.fiap.amanaje.clientes.model.Cliente;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

	@Mock
	private ClienteRepository clienteRepository;

	@InjectMocks
	private ClienteService clienteService;

	@Test
	void shouldInactivateClienteWithoutDeletingIt() {
		Cliente cliente = Cliente.builder()
				.idCliente(1L)
				.stAtivo("S")
				.build();
		when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

		clienteService.inativar(1L);

		ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
		verify(clienteRepository).save(captor.capture());
		assertThat(captor.getValue().getStAtivo()).isEqualTo("N");
		assertThat(captor.getValue().getDtDelEm()).isNotNull();
		assertThat(captor.getValue().getDtAtualizadoEm()).isNotNull();
	}

}
