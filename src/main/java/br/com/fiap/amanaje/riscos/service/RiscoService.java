package br.com.fiap.amanaje.riscos.service;

import br.com.fiap.amanaje.alertas.service.AlertaService;

import br.com.fiap.amanaje.riscos.enums.TipoRisco;

import br.com.fiap.amanaje.riscos.enums.NivelRisco;

import br.com.fiap.amanaje.riscos.repository.AvaliacaoRiscoRepository;

import br.com.fiap.amanaje.riscos.model.AvaliacaoRisco;

import br.com.fiap.amanaje.observacoes.repository.ObservacaoClimaticaRepository;

import br.com.fiap.amanaje.leituras.repository.LeituraIotRepository;

import br.com.fiap.amanaje.regioes.service.RegiaoMonitoradaService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import br.com.fiap.amanaje.alertas.service.AlertaService;
import br.com.fiap.amanaje.alertas.dto.AlertaResponse;
import br.com.fiap.amanaje.common.exception.BusinessRuleException;
import br.com.fiap.amanaje.common.exception.ResourceNotFoundException;
import br.com.fiap.amanaje.leituras.model.LeituraIot;
import br.com.fiap.amanaje.leituras.repository.LeituraIotRepository;
import br.com.fiap.amanaje.observacoes.model.ObservacaoClimatica;
import br.com.fiap.amanaje.observacoes.repository.ObservacaoClimaticaRepository;
import br.com.fiap.amanaje.regioes.service.RegiaoMonitoradaService;
import br.com.fiap.amanaje.riscos.dto.AvaliacaoRiscoResponse;
import br.com.fiap.amanaje.riscos.dto.AvaliarRiscoResponse;
import br.com.fiap.amanaje.riscos.dto.RiscoAtualResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RiscoService {

	private static final String VALIDA = "S";
	private static final long OBSERVACAO_CLIMATICA_MAX_AGE_HOURS = 6;

	private final AvaliacaoRiscoRepository avaliacaoRepository;
	private final LeituraIotRepository leituraRepository;
	private final ObservacaoClimaticaRepository observacaoRepository;
	private final RegiaoMonitoradaService regiaoService;
	private final AlertaService alertaService;

	public RiscoService(
			AvaliacaoRiscoRepository avaliacaoRepository,
			LeituraIotRepository leituraRepository,
			ObservacaoClimaticaRepository observacaoRepository,
			RegiaoMonitoradaService regiaoService,
			AlertaService alertaService) {
		this.avaliacaoRepository = avaliacaoRepository;
		this.leituraRepository = leituraRepository;
		this.observacaoRepository = observacaoRepository;
		this.regiaoService = regiaoService;
		this.alertaService = alertaService;
	}

	@Transactional
	public AvaliarRiscoResponse avaliar(Long idRegiao) {
		regiaoService.buscarAtiva(idRegiao);
		LocalDateTime agora = LocalDateTime.now();
		Optional<LeituraIot> leitura = leituraRepository
				.findFirstByIdRegiaoAndStValidaOrderByDtLeituraDesc(idRegiao, VALIDA);
		Optional<ObservacaoClimatica> ultimaObservacao = observacaoRepository
				.findFirstByIdRegiaoOrderByDtObservacaoDescDtCriadoEmDesc(idRegiao);
		Optional<ObservacaoClimatica> observacao = ultimaObservacao
				.filter(item -> isObservacaoClimaticaRecente(item, agora));
		boolean observacaoClimaticaIgnorada = ultimaObservacao.isPresent() && observacao.isEmpty();
		if (leitura.isEmpty() && observacao.isEmpty()) {
			throw new BusinessRuleException("Não há leitura IoT válida nem observação climática para avaliar a região: "
					+ idRegiao);
		}

		List<AvaliacaoRisco> avaliacoes = List.of(
				criarAvaliacao(idRegiao, TipoRisco.ENCHENTE, leitura.orElse(null), observacao.orElse(null), agora,
						observacaoClimaticaIgnorada),
				criarAvaliacao(idRegiao, TipoRisco.DESLIZAMENTO, leitura.orElse(null), observacao.orElse(null), agora,
						observacaoClimaticaIgnorada),
				criarAvaliacao(idRegiao, TipoRisco.TEMPESTADE, leitura.orElse(null), observacao.orElse(null), agora,
						observacaoClimaticaIgnorada),
				criarAvaliacao(idRegiao, TipoRisco.QUALIDADE_AR, leitura.orElse(null), observacao.orElse(null), agora,
						observacaoClimaticaIgnorada));

		List<AlertaResponse> alertas = avaliacoes.stream()
				.map(alertaService::gerarSeNecessario)
				.flatMap(Optional::stream)
				.toList();

		return new AvaliarRiscoResponse(
				idRegiao,
				avaliacoes.stream().map(this::toResponse).toList(),
				alertas);
	}

	@Transactional(readOnly = true)
	public RiscoAtualResponse buscarAtual(Long idRegiao) {
		regiaoService.buscarAtiva(idRegiao);
		List<AvaliacaoRiscoResponse> avaliacoes = List.of(TipoRisco.values()).stream()
				.map(tipoRisco -> avaliacaoRepository
						.findFirstByIdRegiaoAndTipoRiscoOrderByDtAvaliacaoDesc(idRegiao, tipoRisco))
				.flatMap(Optional::stream)
				.map(this::toResponse)
				.toList();
		if (avaliacoes.isEmpty()) {
			throw new ResourceNotFoundException("Nenhuma avaliação de risco encontrada para a região: " + idRegiao);
		}

		AvaliacaoRiscoResponse consolidada = avaliacoes.stream()
				.max(Comparator.comparing(AvaliacaoRiscoResponse::scoreRisco))
				.orElseThrow();
		return new RiscoAtualResponse(
				idRegiao,
				consolidada.scoreRisco(),
				consolidada.nivelRisco(),
				avaliacoes);
	}

	BigDecimal calculateEnchenteScore(LeituraIot leitura, ObservacaoClimatica observacao) {
		// IoT water telemetry is primary; recent rainfall can raise flood risk when available.
		double score = contribution(leitura == null ? null : leitura.getNivelAguaPercentual(), 100, 60)
				+ inverseContribution(leitura == null ? null : leitura.getDistanciaAguaCm(), 100, 25)
				+ contribution(observacao == null ? null : observacao.getPrecipitacaoMm(), 100, 25);
		return clampScore(score);
	}

	BigDecimal calculateDeslizamentoScore(LeituraIot leitura, ObservacaoClimatica observacao) {
		// Movement sensors are primary; rainfall and humidity add terrain saturation context.
		double score = contribution(leitura == null ? null : leitura.getInclinacaoGraus(), 45, 45)
				+ contribution(leitura == null ? null : leitura.getVibracao(), 10, 30)
				+ contribution(observacao == null ? null : observacao.getPrecipitacaoMm(), 100, 25)
				+ contribution(observacao == null ? null : observacao.getUmidadePercentual(), 100, 10);
		return clampScore(score);
	}

	BigDecimal calculateTempestadeScore(LeituraIot leitura, ObservacaoClimatica observacao) {
		BigDecimal pressao = leitura != null && leitura.getPressaoHpa() != null
				? leitura.getPressaoHpa()
				: observacao == null ? null : observacao.getPressaoHpa();
		// Pressure is single-sourced to avoid double counting; wind and rain come from climate observation.
		double score = pressureContribution(pressao)
				+ contribution(observacao == null ? null : observacao.getVentoKmh(), 100, 35)
				+ contribution(observacao == null ? null : observacao.getPrecipitacaoMm(), 100, 25);
		return clampScore(score);
	}

	BigDecimal calculateQualidadeArScore(LeituraIot leitura, ObservacaoClimatica observacao) {
		double baseScore = contribution(leitura == null ? null : leitura.getPm25(), 75, 60)
				+ contribution(leitura == null ? null : leitura.getPm10(), 150, 40);
		// PM2.5/PM10 remain primary; weather only applies a small dispersion modifier.
		double modifier = qualidadeArMeteoModifier(observacao);
		return clampScore(baseScore + modifier);
	}

	BigDecimal clampScore(double score) {
		return BigDecimal.valueOf(Math.max(0, Math.min(100, score)))
				.setScale(2, RoundingMode.HALF_UP);
	}

	NivelRisco resolveNivelRisco(BigDecimal score) {
		if (score.compareTo(BigDecimal.valueOf(75)) >= 0) {
			return NivelRisco.CRITICO;
		}
		if (score.compareTo(BigDecimal.valueOf(50)) >= 0) {
			return NivelRisco.ALTO;
		}
		if (score.compareTo(BigDecimal.valueOf(25)) >= 0) {
			return NivelRisco.MODERADO;
		}
		return NivelRisco.BAIXO;
	}

	private AvaliacaoRisco criarAvaliacao(
			Long idRegiao,
			TipoRisco tipoRisco,
			LeituraIot leitura,
			ObservacaoClimatica observacao,
			LocalDateTime agora,
			boolean observacaoClimaticaIgnorada) {
		BigDecimal score = switch (tipoRisco) {
			case ENCHENTE -> calculateEnchenteScore(leitura, observacao);
			case DESLIZAMENTO -> calculateDeslizamentoScore(leitura, observacao);
			case TEMPESTADE -> calculateTempestadeScore(leitura, observacao);
			case QUALIDADE_AR -> calculateQualidadeArScore(leitura, observacao);
		};
		AvaliacaoRisco avaliacao = AvaliacaoRisco.builder()
				.idRegiao(idRegiao)
				.idLeitura(leitura == null ? null : leitura.getIdLeitura())
				.idObservacao(observacao == null ? null : observacao.getIdObservacao())
				.tipoRisco(tipoRisco)
				.scoreRisco(score)
				.nivelRisco(resolveNivelRisco(score))
				.motivo(criarMotivo(tipoRisco, leitura, observacao, observacaoClimaticaIgnorada))
				.dtAvaliacao(agora)
				.build();
		return avaliacaoRepository.save(avaliacao);
	}

	private double contribution(BigDecimal value, double referenceValue, double maxContribution) {
		if (value == null) {
			return 0;
		}
		return Math.min(Math.abs(value.doubleValue()) / referenceValue * maxContribution, maxContribution);
	}

	private double inverseContribution(BigDecimal value, double referenceValue, double maxContribution) {
		if (value == null) {
			return 0;
		}
		double normalized = (referenceValue - value.doubleValue()) / referenceValue * maxContribution;
		return Math.max(0, Math.min(normalized, maxContribution));
	}

	private double pressureContribution(BigDecimal pressaoHpa) {
		if (pressaoHpa == null) {
			return 0;
		}
		double normalized = (1013 - pressaoHpa.doubleValue()) / 213 * 40;
		return Math.max(0, Math.min(normalized, 40));
	}

	private double qualidadeArMeteoModifier(ObservacaoClimatica observacao) {
		if (observacao == null) {
			return 0;
		}
		double modifier = 0;
		BigDecimal vento = observacao.getVentoKmh();
		if (vento != null && vento.compareTo(BigDecimal.valueOf(5)) < 0) {
			modifier += 10;
		}
		else if (vento != null && vento.compareTo(BigDecimal.valueOf(10)) < 0) {
			modifier += 5;
		}
		BigDecimal umidade = observacao.getUmidadePercentual();
		if (umidade != null && umidade.compareTo(BigDecimal.valueOf(90)) >= 0) {
			modifier += 5;
		}
		else if (umidade != null && umidade.compareTo(BigDecimal.valueOf(80)) >= 0) {
			modifier += 3;
		}
		BigDecimal chuva = observacao.getPrecipitacaoMm();
		if (chuva != null && chuva.compareTo(BigDecimal.valueOf(10)) >= 0) {
			modifier -= 10;
		}
		else if (chuva != null && chuva.compareTo(BigDecimal.valueOf(2.5)) >= 0) {
			modifier -= 5;
		}
		return Math.max(-10, Math.min(15, modifier));
	}

	private boolean isObservacaoClimaticaRecente(ObservacaoClimatica observacao, LocalDateTime agora) {
		LocalDateTime dataReferencia = observacao.getDtObservacao() != null
				? observacao.getDtObservacao()
				: observacao.getDtCriadoEm();
		return dataReferencia != null
				&& !dataReferencia.isAfter(agora)
				&& !dataReferencia.isBefore(agora.minusHours(OBSERVACAO_CLIMATICA_MAX_AGE_HOURS));
	}

	private String criarMotivo(
			TipoRisco tipoRisco,
			LeituraIot leitura,
			ObservacaoClimatica observacao,
			boolean observacaoClimaticaIgnorada) {
		String origem = criarDescricaoOrigem(leitura, observacao, observacaoClimaticaIgnorada);
		return switch (tipoRisco) {
			case ENCHENTE -> "Risco calculado com base em leitura IoT de nível da água e precipitação observada. "
					+ origem;
			case DESLIZAMENTO -> "Risco calculado com base em inclinação, vibração, precipitação e umidade observadas. "
					+ origem;
			case TEMPESTADE -> "Risco calculado com base em pressão atmosférica, vento e precipitação observadas. "
					+ origem;
			case QUALIDADE_AR -> "Risco de qualidade do ar baseado em PM2.5/PM10, ajustado por condições meteorológicas. "
					+ origem;
		};
	}

	private String criarDescricaoOrigem(
			LeituraIot leitura,
			ObservacaoClimatica observacao,
			boolean observacaoClimaticaIgnorada) {
		if (leitura != null && observacao != null) {
			return "Foram usadas telemetria IoT válida e observação climática recente.";
		}
		if (leitura != null) {
			return observacaoClimaticaIgnorada
					? "Sem observação climática recente; cálculo baseado apenas em telemetria IoT."
					: "Sem observação climática disponível; cálculo baseado apenas em telemetria IoT.";
		}
		return "Sem telemetria IoT válida recente; cálculo baseado apenas em observação climática recente.";
	}

	private AvaliacaoRiscoResponse toResponse(AvaliacaoRisco avaliacao) {
		return new AvaliacaoRiscoResponse(
				avaliacao.getIdAvaliacao(),
				avaliacao.getIdRegiao(),
				avaliacao.getIdLeitura(),
				avaliacao.getIdObservacao(),
				avaliacao.getTipoRisco(),
				avaliacao.getScoreRisco(),
				avaliacao.getNivelRisco(),
				avaliacao.getMotivo(),
				avaliacao.getDtAvaliacao());
	}

}
