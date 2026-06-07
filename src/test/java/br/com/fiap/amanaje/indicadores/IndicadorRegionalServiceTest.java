package br.com.fiap.amanaje.indicadores;

import br.com.fiap.amanaje.indicadores.repository.IndicadorRegionalRepository;

import br.com.fiap.amanaje.indicadores.service.IndicadorRegionalService;

import br.com.fiap.amanaje.indicadores.model.IndicadorRegional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import br.com.fiap.amanaje.indicadores.dto.IndicadorRegionalResponse;
import br.com.fiap.amanaje.riscos.enums.NivelRisco;
import br.com.fiap.amanaje.riscos.enums.TipoRisco;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IndicadorRegionalServiceTest {

	@Mock
	private IndicadorRegionalRepository indicadorRepository;

	@InjectMocks
	private IndicadorRegionalService indicadorService;

	@Test
	void shouldFilterPersistedIndicatorsIgnoringLocationCase() {
		when(indicadorRepository.findAll()).thenReturn(List.of(
				indicador(1L, "SP", "Santos", TipoRisco.ENCHENTE, NivelRisco.ALTO),
				indicador(2L, "RJ", "Rio de Janeiro", TipoRisco.TEMPESTADE, NivelRisco.BAIXO)));

		List<IndicadorRegionalResponse> response = indicadorService.listar(
				"sp",
				"SANTOS",
				TipoRisco.ENCHENTE,
				NivelRisco.ALTO);

		assertThat(response).singleElement()
				.satisfies(indicador -> {
					assertThat(indicador.idIndicador()).isEqualTo(1L);
					assertThat(indicador.scoreMedio()).isEqualByComparingTo(BigDecimal.valueOf(60));
				});
	}

	@Test
	void shouldReturnEmptyListWhenThereAreNoPersistedIndicators() {
		when(indicadorRepository.findAll()).thenReturn(List.of());

		assertThat(indicadorService.listar(null, null, null, null)).isEmpty();
	}

	private IndicadorRegional indicador(
			Long idIndicador,
			String estado,
			String cidade,
			TipoRisco tipoRisco,
			NivelRisco nivelRiscoMedio) {
		return IndicadorRegional.builder()
				.idIndicador(idIndicador)
				.idRegiao(idIndicador + 10)
				.estado(estado)
				.cidade(cidade)
				.nomeRegiao("Região " + idIndicador)
				.tipoRisco(tipoRisco)
				.scoreMedio(BigDecimal.valueOf(60))
				.nivelRiscoMedio(nivelRiscoMedio)
				.quantidadeEstacoes(2)
				.quantidadeAlertasAtivos(1)
				.fonteCalculo("PLSQL")
				.build();
	}

}
