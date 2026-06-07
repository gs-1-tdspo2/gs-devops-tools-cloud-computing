package br.com.fiap.amanaje.riscos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import br.com.fiap.amanaje.alertas.service.AlertaService;
import br.com.fiap.amanaje.common.exception.BusinessRuleException;
import br.com.fiap.amanaje.leituras.model.LeituraIot;
import br.com.fiap.amanaje.leituras.repository.LeituraIotRepository;
import br.com.fiap.amanaje.observacoes.model.ObservacaoClimatica;
import br.com.fiap.amanaje.observacoes.repository.ObservacaoClimaticaRepository;
import br.com.fiap.amanaje.regioes.model.RegiaoMonitorada;
import br.com.fiap.amanaje.regioes.service.RegiaoMonitoradaService;
import br.com.fiap.amanaje.riscos.dto.AvaliarRiscoResponse;
import br.com.fiap.amanaje.riscos.dto.RiscoAtualResponse;
import br.com.fiap.amanaje.riscos.enums.NivelRisco;
import br.com.fiap.amanaje.riscos.enums.TipoRisco;
import br.com.fiap.amanaje.riscos.model.AvaliacaoRisco;
import br.com.fiap.amanaje.riscos.repository.AvaliacaoRiscoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RiscoServiceTest {

	@Mock
	private AvaliacaoRiscoRepository avaliacaoRepository;

	@Mock
	private LeituraIotRepository leituraRepository;

	@Mock
	private ObservacaoClimaticaRepository observacaoRepository;

	@Mock
	private RegiaoMonitoradaService regiaoService;

	@Mock
	private AlertaService alertaService;

	@InjectMocks
	private RiscoService riscoService;

	@Test
	void shouldRejectEvaluationWithoutAnySourceData() {
		when(regiaoService.buscarAtiva(20L)).thenReturn(RegiaoMonitorada.builder().idRegiao(20L).build());
		when(leituraRepository.findFirstByIdRegiaoAndStValidaOrderByDtLeituraDesc(20L, "S"))
				.thenReturn(Optional.empty());
		when(observacaoRepository.findFirstByIdRegiaoOrderByDtObservacaoDescDtCriadoEmDesc(20L))
				.thenReturn(Optional.empty());

		assertThatThrownBy(() -> riscoService.avaliar(20L))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("Não há leitura IoT válida nem observação climática para avaliar a região: 20");
	}

	@Test
	void shouldCreateFourEvaluationsFromIotOnlySource() {
		LeituraIot leitura = leituraMaxima().build();
		when(regiaoService.buscarAtiva(20L)).thenReturn(RegiaoMonitorada.builder().idRegiao(20L).build());
		when(leituraRepository.findFirstByIdRegiaoAndStValidaOrderByDtLeituraDesc(20L, "S"))
				.thenReturn(Optional.of(leitura));
		when(observacaoRepository.findFirstByIdRegiaoOrderByDtObservacaoDescDtCriadoEmDesc(20L))
				.thenReturn(Optional.empty());
		when(avaliacaoRepository.save(any(AvaliacaoRisco.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));
		when(alertaService.gerarSeNecessario(any(AvaliacaoRisco.class)))
				.thenReturn(Optional.empty());

		AvaliarRiscoResponse response = riscoService.avaliar(20L);

		assertThat(response.avaliacoes()).hasSize(4);
		assertThat(response.avaliacoes())
				.allSatisfy(avaliacao -> {
					assertThat(avaliacao.idLeitura()).isEqualTo(30L);
					assertThat(avaliacao.idObservacao()).isNull();
				});
		assertThat(score(response, TipoRisco.ENCHENTE)).isEqualByComparingTo("85.00");
		assertThat(score(response, TipoRisco.DESLIZAMENTO)).isEqualByComparingTo("75.00");
		assertThat(score(response, TipoRisco.TEMPESTADE)).isEqualByComparingTo("40.00");
		assertThat(score(response, TipoRisco.QUALIDADE_AR)).isEqualByComparingTo("100.00");
		verify(avaliacaoRepository, times(4)).save(any(AvaliacaoRisco.class));
		verify(alertaService, times(4)).gerarSeNecessario(any(AvaliacaoRisco.class));
	}

	@Test
	void shouldCreateClimateOnlyEvaluationsWithoutIotReading() {
		ObservacaoClimatica observacao = observacaoRecente()
				.precipitacaoMm(BigDecimal.valueOf(100))
				.umidadePercentual(BigDecimal.valueOf(100))
				.ventoKmh(BigDecimal.valueOf(100))
				.pressaoHpa(BigDecimal.valueOf(800))
				.build();
		when(regiaoService.buscarAtiva(20L)).thenReturn(RegiaoMonitorada.builder().idRegiao(20L).build());
		when(leituraRepository.findFirstByIdRegiaoAndStValidaOrderByDtLeituraDesc(20L, "S"))
				.thenReturn(Optional.empty());
		when(observacaoRepository.findFirstByIdRegiaoOrderByDtObservacaoDescDtCriadoEmDesc(20L))
				.thenReturn(Optional.of(observacao));
		when(avaliacaoRepository.save(any(AvaliacaoRisco.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));
		when(alertaService.gerarSeNecessario(any(AvaliacaoRisco.class)))
				.thenReturn(Optional.empty());

		AvaliarRiscoResponse response = riscoService.avaliar(20L);

		assertThat(response.avaliacoes()).hasSize(4);
		assertThat(response.avaliacoes())
				.allSatisfy(avaliacao -> {
					assertThat(avaliacao.idLeitura()).isNull();
					assertThat(avaliacao.idObservacao()).isEqualTo(40L);
				});
		assertThat(score(response, TipoRisco.ENCHENTE)).isEqualByComparingTo("25.00");
		assertThat(score(response, TipoRisco.DESLIZAMENTO)).isEqualByComparingTo("35.00");
		assertThat(score(response, TipoRisco.TEMPESTADE)).isEqualByComparingTo("100.00");
		assertThat(score(response, TipoRisco.QUALIDADE_AR)).isEqualByComparingTo("0.00");
	}

	@Test
	void shouldIncreaseEnchenteWithHighPrecipitation() {
		LeituraIot leitura = LeituraIot.builder()
				.nivelAguaPercentual(BigDecimal.valueOf(40))
				.distanciaAguaCm(BigDecimal.valueOf(80))
				.build();

		BigDecimal semChuva = riscoService.calculateEnchenteScore(leitura, null);
		BigDecimal comChuva = riscoService.calculateEnchenteScore(
				leitura,
				observacaoRecente().precipitacaoMm(BigDecimal.valueOf(100)).build());

		assertThat(comChuva).isGreaterThan(semChuva);
		assertThat(comChuva.subtract(semChuva)).isEqualByComparingTo("25.00");
	}

	@Test
	void shouldIncreaseDeslizamentoWithHighPrecipitationAndHumidity() {
		LeituraIot leitura = LeituraIot.builder()
				.inclinacaoGraus(BigDecimal.valueOf(20))
				.vibracao(BigDecimal.valueOf(2))
				.build();

		BigDecimal semClima = riscoService.calculateDeslizamentoScore(leitura, null);
		BigDecimal comClima = riscoService.calculateDeslizamentoScore(
				leitura,
				observacaoRecente()
						.precipitacaoMm(BigDecimal.valueOf(100))
						.umidadePercentual(BigDecimal.valueOf(100))
						.build());

		assertThat(comClima).isGreaterThan(semClima);
		assertThat(comClima.subtract(semClima)).isEqualByComparingTo("35.00");
	}

	@Test
	void shouldIncreaseTempestadeWithPressureWindAndPrecipitation() {
		BigDecimal semClima = riscoService.calculateTempestadeScore(null, null);
		BigDecimal comClima = riscoService.calculateTempestadeScore(
				null,
				observacaoRecente()
						.pressaoHpa(BigDecimal.valueOf(800))
						.ventoKmh(BigDecimal.valueOf(100))
						.precipitacaoMm(BigDecimal.valueOf(100))
						.build());

		assertThat(semClima).isEqualByComparingTo("0.00");
		assertThat(comClima).isEqualByComparingTo("100.00");
	}

	@Test
	void shouldKeepQualidadeArPmScorePrimaryWithBoundedMeteoModifier() {
		ObservacaoClimatica piorDispersao = observacaoRecente()
				.ventoKmh(BigDecimal.ZERO)
				.umidadePercentual(BigDecimal.valueOf(95))
				.precipitacaoMm(BigDecimal.ZERO)
				.build();

		assertThat(riscoService.calculateQualidadeArScore(
				LeituraIot.builder().pm25(BigDecimal.ZERO).pm10(BigDecimal.ZERO).build(),
				piorDispersao))
				.isEqualByComparingTo("15.00");
		assertThat(riscoService.resolveNivelRisco(BigDecimal.valueOf(15))).isEqualTo(NivelRisco.BAIXO);
		assertThat(riscoService.calculateQualidadeArScore(
				LeituraIot.builder().pm25(BigDecimal.valueOf(118)).pm10(BigDecimal.valueOf(180)).build(),
				piorDispersao))
				.isEqualByComparingTo("100.00");
		assertThat(riscoService.calculateQualidadeArScore(LeituraIot.builder().build(), piorDispersao))
				.isEqualByComparingTo("15.00");
	}

	@Test
	void shouldIgnoreStaleClimateObservation() {
		LeituraIot leitura = leituraMaxima()
				.pm25(BigDecimal.ZERO)
				.pm10(BigDecimal.ZERO)
				.build();
		ObservacaoClimatica stale = observacaoRecente()
				.dtObservacao(LocalDateTime.now().minusHours(7))
				.dtCriadoEm(LocalDateTime.now().minusHours(7))
				.precipitacaoMm(BigDecimal.valueOf(100))
				.ventoKmh(BigDecimal.ZERO)
				.umidadePercentual(BigDecimal.valueOf(100))
				.build();
		when(regiaoService.buscarAtiva(20L)).thenReturn(RegiaoMonitorada.builder().idRegiao(20L).build());
		when(leituraRepository.findFirstByIdRegiaoAndStValidaOrderByDtLeituraDesc(20L, "S"))
				.thenReturn(Optional.of(leitura));
		when(observacaoRepository.findFirstByIdRegiaoOrderByDtObservacaoDescDtCriadoEmDesc(20L))
				.thenReturn(Optional.of(stale));
		when(avaliacaoRepository.save(any(AvaliacaoRisco.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));
		when(alertaService.gerarSeNecessario(any(AvaliacaoRisco.class)))
				.thenReturn(Optional.empty());

		AvaliarRiscoResponse response = riscoService.avaliar(20L);

		assertThat(response.avaliacoes())
				.allSatisfy(avaliacao -> assertThat(avaliacao.idObservacao()).isNull());
		assertThat(score(response, TipoRisco.ENCHENTE)).isEqualByComparingTo("85.00");
		assertThat(score(response, TipoRisco.DESLIZAMENTO)).isEqualByComparingTo("75.00");
		assertThat(score(response, TipoRisco.QUALIDADE_AR)).isEqualByComparingTo("0.00");
	}

	@Test
	void shouldResolveExactRiskBoundaries() {
		assertThat(riscoService.resolveNivelRisco(BigDecimal.valueOf(24))).isEqualTo(NivelRisco.BAIXO);
		assertThat(riscoService.resolveNivelRisco(BigDecimal.valueOf(25))).isEqualTo(NivelRisco.MODERADO);
		assertThat(riscoService.resolveNivelRisco(BigDecimal.valueOf(50))).isEqualTo(NivelRisco.ALTO);
		assertThat(riscoService.resolveNivelRisco(BigDecimal.valueOf(75))).isEqualTo(NivelRisco.CRITICO);
		assertThat(riscoService.clampScore(120)).isEqualByComparingTo(BigDecimal.valueOf(100));
		assertThat(riscoService.clampScore(-10)).isEqualByComparingTo(BigDecimal.ZERO);
	}

	@Test
	void shouldReturnHighestLatestRiskAsConsolidatedCurrentRisk() {
		when(regiaoService.buscarAtiva(20L)).thenReturn(RegiaoMonitorada.builder().idRegiao(20L).build());
		when(avaliacaoRepository.findFirstByIdRegiaoAndTipoRiscoOrderByDtAvaliacaoDesc(20L, TipoRisco.ENCHENTE))
				.thenReturn(Optional.of(avaliacao(TipoRisco.ENCHENTE, 60)));
		when(avaliacaoRepository.findFirstByIdRegiaoAndTipoRiscoOrderByDtAvaliacaoDesc(20L, TipoRisco.DESLIZAMENTO))
				.thenReturn(Optional.of(avaliacao(TipoRisco.DESLIZAMENTO, 30)));
		when(avaliacaoRepository.findFirstByIdRegiaoAndTipoRiscoOrderByDtAvaliacaoDesc(20L, TipoRisco.TEMPESTADE))
				.thenReturn(Optional.of(avaliacao(TipoRisco.TEMPESTADE, 80)));
		when(avaliacaoRepository.findFirstByIdRegiaoAndTipoRiscoOrderByDtAvaliacaoDesc(20L, TipoRisco.QUALIDADE_AR))
				.thenReturn(Optional.of(avaliacao(TipoRisco.QUALIDADE_AR, 10)));

		RiscoAtualResponse response = riscoService.buscarAtual(20L);

		assertThat(response.avaliacoes()).hasSize(4);
		assertThat(response.scoreConsolidado()).isEqualByComparingTo(BigDecimal.valueOf(80));
		assertThat(response.nivelConsolidado()).isEqualTo(NivelRisco.CRITICO);
	}

	private BigDecimal score(AvaliarRiscoResponse response, TipoRisco tipoRisco) {
		return response.avaliacoes().stream()
				.filter(avaliacao -> avaliacao.tipoRisco() == tipoRisco)
				.findFirst()
				.orElseThrow()
				.scoreRisco();
	}

	private LeituraIot.LeituraIotBuilder leituraMaxima() {
		return LeituraIot.builder()
				.idLeitura(30L)
				.nivelAguaPercentual(BigDecimal.valueOf(100))
				.distanciaAguaCm(BigDecimal.ZERO)
				.inclinacaoGraus(BigDecimal.valueOf(45))
				.vibracao(BigDecimal.valueOf(10))
				.pressaoHpa(BigDecimal.valueOf(800))
				.pm25(BigDecimal.valueOf(75))
				.pm10(BigDecimal.valueOf(150));
	}

	private ObservacaoClimatica.ObservacaoClimaticaBuilder observacaoRecente() {
		LocalDateTime agora = LocalDateTime.now();
		return ObservacaoClimatica.builder()
				.idObservacao(40L)
				.dtObservacao(agora)
				.dtCriadoEm(agora);
	}

	private AvaliacaoRisco avaliacao(TipoRisco tipoRisco, int score) {
		BigDecimal scoreRisco = BigDecimal.valueOf(score);
		return AvaliacaoRisco.builder()
				.idRegiao(20L)
				.tipoRisco(tipoRisco)
				.scoreRisco(scoreRisco)
				.nivelRisco(riscoService.resolveNivelRisco(scoreRisco))
				.build();
	}

}
