package br.com.fiap.amanaje.observacoes;

import br.com.fiap.amanaje.observacoes.repository.ObservacaoClimaticaRepository;

import br.com.fiap.amanaje.observacoes.service.ObservacaoClimaticaService;

import br.com.fiap.amanaje.observacoes.model.ObservacaoClimatica;

import br.com.fiap.amanaje.regioes.service.RegiaoMonitoradaService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import br.com.fiap.amanaje.common.exception.ResourceNotFoundException;
import br.com.fiap.amanaje.observacoes.dto.ObservacaoClimaticaCreateRequest;
import br.com.fiap.amanaje.observacoes.dto.ObservacaoClimaticaResponse;
import br.com.fiap.amanaje.regioes.model.RegiaoMonitorada;
import br.com.fiap.amanaje.regioes.service.RegiaoMonitoradaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ObservacaoClimaticaServiceTest {

	@Mock
	private ObservacaoClimaticaRepository observacaoRepository;

	@Mock
	private RegiaoMonitoradaService regiaoService;

	@InjectMocks
	private ObservacaoClimaticaService observacaoService;

	@Test
	void shouldSaveClimateObservationWithZeroMetricAndDefaultTimestamps() {
		ObservacaoClimaticaCreateRequest request = new ObservacaoClimaticaCreateRequest(
				20L,
				"Servico Climatico C#",
				null,
				null,
				BigDecimal.ZERO,
				null,
				null,
				null,
				null,
				null);
		when(regiaoService.buscarAtiva(20L)).thenReturn(RegiaoMonitorada.builder().idRegiao(20L).build());
		when(observacaoRepository.save(any(ObservacaoClimatica.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		ObservacaoClimaticaResponse response = observacaoService.criar(request);

		assertThat(response.idRegiao()).isEqualTo(20L);
		assertThat(response.precipitacao()).isEqualByComparingTo(BigDecimal.ZERO);
		assertThat(response.dtObservacao()).isNotNull();
		assertThat(response.dtCriadoEm()).isNotNull();
	}

	@Test
	void shouldReturnNotFoundWhenRegionHasNoClimateObservation() {
		when(regiaoService.buscarAtiva(20L)).thenReturn(RegiaoMonitorada.builder().idRegiao(20L).build());
		when(observacaoRepository.findFirstByIdRegiaoOrderByDtObservacaoDescDtCriadoEmDesc(20L))
				.thenReturn(Optional.empty());

		assertThatThrownBy(() -> observacaoService.buscarUltimaPorRegiao(20L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Nenhuma observação climática encontrada para a região: 20");
	}

}
