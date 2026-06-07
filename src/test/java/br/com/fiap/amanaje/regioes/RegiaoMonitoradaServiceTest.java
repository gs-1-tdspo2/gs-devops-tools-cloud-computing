package br.com.fiap.amanaje.regioes;

import br.com.fiap.amanaje.regioes.enums.TipoVisibilidade;

import br.com.fiap.amanaje.regioes.enums.TipoArea;

import br.com.fiap.amanaje.regioes.repository.RegiaoMonitoradaRepository;

import br.com.fiap.amanaje.regioes.service.RegiaoMonitoradaService;

import br.com.fiap.amanaje.clientes.service.ClienteService;

import br.com.fiap.amanaje.clientes.model.Cliente;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import br.com.fiap.amanaje.clientes.service.ClienteService;
import br.com.fiap.amanaje.common.exception.ResourceNotFoundException;
import br.com.fiap.amanaje.regioes.dto.RegiaoCreateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegiaoMonitoradaServiceTest {

	@Mock
	private RegiaoMonitoradaRepository regiaoRepository;

	@Mock
	private ClienteService clienteService;

	@InjectMocks
	private RegiaoMonitoradaService regiaoService;

	@Test
	void shouldRejectRegiaoWhenClienteIsNotActive() {
		RegiaoCreateRequest request = new RegiaoCreateRequest(
				99L,
				"Encosta Norte",
				"São Paulo",
				"SP",
				new BigDecimal("-23.550520"),
				new BigDecimal("-46.633308"),
				TipoArea.ENCOSTA,
				70,
				TipoVisibilidade.INSTITUCIONAL);
		when(clienteService.buscarAtivo(99L))
				.thenThrow(new ResourceNotFoundException("Cliente ativo não encontrado: 99"));

		assertThatThrownBy(() -> regiaoService.criar(request))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Cliente ativo não encontrado: 99");
	}

}
