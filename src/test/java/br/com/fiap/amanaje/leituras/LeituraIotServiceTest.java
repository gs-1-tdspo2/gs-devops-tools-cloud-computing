package br.com.fiap.amanaje.leituras;

import br.com.fiap.amanaje.leituras.repository.LeituraIotRepository;

import br.com.fiap.amanaje.leituras.service.LeituraIotService;

import br.com.fiap.amanaje.leituras.model.LeituraIot;

import br.com.fiap.amanaje.estacoes.repository.EstacaoIotRepository;

import br.com.fiap.amanaje.estacoes.service.EstacaoIotService;

import br.com.fiap.amanaje.regioes.service.RegiaoMonitoradaService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import br.com.fiap.amanaje.common.exception.BusinessRuleException;
import br.com.fiap.amanaje.estacoes.model.EstacaoIot;
import br.com.fiap.amanaje.estacoes.repository.EstacaoIotRepository;
import br.com.fiap.amanaje.estacoes.service.EstacaoIotService;
import br.com.fiap.amanaje.leituras.dto.LeituraIotCreateRequest;
import br.com.fiap.amanaje.leituras.dto.LeituraIotResponse;
import br.com.fiap.amanaje.regioes.service.RegiaoMonitoradaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeituraIotServiceTest {

	@Mock
	private LeituraIotRepository leituraRepository;

	@Mock
	private EstacaoIotRepository estacaoRepository;

	@Mock
	private EstacaoIotService estacaoService;

	@Mock
	private RegiaoMonitoradaService regiaoService;

	@InjectMocks
	private LeituraIotService leituraService;

	@Test
	void shouldSaveZeroTelemetryByStationCodeAndUpdateLastCommunication() {
		LeituraIotCreateRequest request = new LeituraIotCreateRequest(
				null,
				"EST-001",
				null,
				BigDecimal.ZERO,
				null,
				null,
				null,
				null,
				null,
				null);
		EstacaoIot estacao = EstacaoIot.builder()
				.idEstacao(10L)
				.idRegiao(20L)
				.build();
		when(estacaoService.buscarAtivaPorCodigo("EST-001")).thenReturn(estacao);
		when(leituraRepository.save(any(LeituraIot.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		LeituraIotResponse response = leituraService.criar(request);

		ArgumentCaptor<EstacaoIot> estacaoCaptor = ArgumentCaptor.forClass(EstacaoIot.class);
		verify(estacaoRepository).save(estacaoCaptor.capture());
		assertThat(response.idEstacao()).isEqualTo(10L);
		assertThat(response.idRegiao()).isEqualTo(20L);
		assertThat(response.distanciaAguaCm()).isEqualByComparingTo(BigDecimal.ZERO);
		assertThat(response.stValida()).isEqualTo("S");
		assertThat(response.dtLeitura()).isNotNull();
		assertThat(response.dtRecebidoEm()).isNotNull();
		assertThat(estacaoCaptor.getValue().getDtUltimaComunicacao()).isNotNull();
	}

	@Test
	void shouldRejectDifferentStationIdentifiers() {
		LeituraIotCreateRequest request = new LeituraIotCreateRequest(
				11L,
				"EST-001",
				null,
				BigDecimal.ONE,
				null,
				null,
				null,
				null,
				null,
				null);
		when(estacaoService.buscarAtivaPorCodigo("EST-001"))
				.thenReturn(EstacaoIot.builder().idEstacao(10L).build());

		assertThatThrownBy(() -> leituraService.criar(request))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("idEstacao e codigoEstacao identificam estações diferentes");
	}

}
