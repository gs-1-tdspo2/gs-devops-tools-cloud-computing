package br.com.fiap.amanaje.leituras.service;

import br.com.fiap.amanaje.leituras.repository.LeituraIotRepository;

import br.com.fiap.amanaje.leituras.model.LeituraIot;

import br.com.fiap.amanaje.estacoes.repository.EstacaoIotRepository;

import br.com.fiap.amanaje.estacoes.service.EstacaoIotService;

import br.com.fiap.amanaje.regioes.service.RegiaoMonitoradaService;

import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.amanaje.common.exception.BusinessRuleException;
import br.com.fiap.amanaje.estacoes.model.EstacaoIot;
import br.com.fiap.amanaje.estacoes.repository.EstacaoIotRepository;
import br.com.fiap.amanaje.estacoes.service.EstacaoIotService;
import br.com.fiap.amanaje.leituras.dto.LeituraIotCreateRequest;
import br.com.fiap.amanaje.leituras.dto.LeituraIotResponse;
import br.com.fiap.amanaje.regioes.service.RegiaoMonitoradaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class LeituraIotService {

	private static final String VALIDA = "S";

	private final LeituraIotRepository leituraRepository;
	private final EstacaoIotRepository estacaoRepository;
	private final EstacaoIotService estacaoService;
	private final RegiaoMonitoradaService regiaoService;

	public LeituraIotService(
			LeituraIotRepository leituraRepository,
			EstacaoIotRepository estacaoRepository,
			EstacaoIotService estacaoService,
			RegiaoMonitoradaService regiaoService) {
		this.leituraRepository = leituraRepository;
		this.estacaoRepository = estacaoRepository;
		this.estacaoService = estacaoService;
		this.regiaoService = regiaoService;
	}

	@Transactional
	public LeituraIotResponse criar(LeituraIotCreateRequest request) {
		validarMetricas(request);
		EstacaoIot estacao = buscarEstacao(request);
		LocalDateTime agora = LocalDateTime.now();
		LeituraIot leitura = LeituraIot.builder()
				.idEstacao(estacao.getIdEstacao())
				.idRegiao(estacao.getIdRegiao())
				.distanciaAguaCm(request.distanciaAguaCm())
				.nivelAguaPercentual(request.nivelAguaPercentual())
				.inclinacaoGraus(request.inclinacaoGraus())
				.vibracao(request.vibracao())
				.pressaoHpa(request.pressaoHpa())
				.pm25(request.pm25())
				.pm10(request.pm10())
				.dtLeitura(request.dtLeitura() == null ? agora : request.dtLeitura())
				.dtRecebidoEm(agora)
				.stValida(VALIDA)
				.build();

		LeituraIot leituraSalva = leituraRepository.save(leitura);
		estacao.setDtUltimaComunicacao(agora);
		estacao.setDtAtualizadoEm(agora);
		estacaoRepository.save(estacao);
		return toResponse(leituraSalva);
	}

	@Transactional(readOnly = true)
	public List<LeituraIotResponse> listarPorRegiao(Long idRegiao) {
		regiaoService.buscarAtiva(idRegiao);
		return leituraRepository.findByIdRegiaoOrderByDtLeituraDesc(idRegiao).stream()
				.map(this::toResponse)
				.toList();
	}

	private EstacaoIot buscarEstacao(LeituraIotCreateRequest request) {
		if (StringUtils.hasText(request.codigoEstacao())) {
			EstacaoIot estacao = estacaoService.buscarAtivaPorCodigo(request.codigoEstacao());
			if (request.idEstacao() != null && !estacao.getIdEstacao().equals(request.idEstacao())) {
				throw new BusinessRuleException("idEstacao e codigoEstacao identificam estações diferentes");
			}
			return estacao;
		}

		if (request.idEstacao() != null) {
			return estacaoService.buscarAtiva(request.idEstacao());
		}

		throw new BusinessRuleException("Informe idEstacao ou codigoEstacao");
	}

	private void validarMetricas(LeituraIotCreateRequest request) {
		if (request.distanciaAguaCm() == null
				&& request.nivelAguaPercentual() == null
				&& request.inclinacaoGraus() == null
				&& request.vibracao() == null
				&& request.pressaoHpa() == null
				&& request.pm25() == null
				&& request.pm10() == null) {
			throw new BusinessRuleException("Informe ao menos uma métrica de telemetria");
		}
	}

	private LeituraIotResponse toResponse(LeituraIot leitura) {
		return new LeituraIotResponse(
				leitura.getIdLeitura(),
				leitura.getIdEstacao(),
				leitura.getIdRegiao(),
				leitura.getDistanciaAguaCm(),
				leitura.getNivelAguaPercentual(),
				leitura.getInclinacaoGraus(),
				leitura.getVibracao(),
				leitura.getPressaoHpa(),
				leitura.getPm25(),
				leitura.getPm10(),
				leitura.getDtLeitura(),
				leitura.getDtRecebidoEm(),
				leitura.getStValida());
	}

}
