package br.com.fiap.amanaje.observacoes.service;

import br.com.fiap.amanaje.observacoes.repository.ObservacaoClimaticaRepository;

import br.com.fiap.amanaje.observacoes.model.ObservacaoClimatica;

import br.com.fiap.amanaje.regioes.service.RegiaoMonitoradaService;

import java.time.LocalDateTime;

import br.com.fiap.amanaje.common.exception.BusinessRuleException;
import br.com.fiap.amanaje.common.exception.ResourceNotFoundException;
import br.com.fiap.amanaje.observacoes.dto.ObservacaoClimaticaCreateRequest;
import br.com.fiap.amanaje.observacoes.dto.ObservacaoClimaticaResponse;
import br.com.fiap.amanaje.regioes.service.RegiaoMonitoradaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObservacaoClimaticaService {

	private final ObservacaoClimaticaRepository observacaoRepository;
	private final RegiaoMonitoradaService regiaoService;

	public ObservacaoClimaticaService(
			ObservacaoClimaticaRepository observacaoRepository,
			RegiaoMonitoradaService regiaoService) {
		this.observacaoRepository = observacaoRepository;
		this.regiaoService = regiaoService;
	}

	@Transactional
	public ObservacaoClimaticaResponse criar(ObservacaoClimaticaCreateRequest request) {
		regiaoService.buscarAtiva(request.idRegiao());
		validarMetricas(request);
		LocalDateTime agora = LocalDateTime.now();
		ObservacaoClimatica observacao = ObservacaoClimatica.builder()
				.idRegiao(request.idRegiao())
				.fonte(request.fonte())
				.temperaturaCelsius(request.temperatura())
				.umidadePercentual(request.umidade())
				.precipitacaoMm(request.precipitacao())
				.ventoKmh(request.vento())
				.pressaoHpa(request.pressaoHpa())
				.radiacaoSolar(request.radiacaoSolar())
				.indiceUv(request.indiceUv())
				.dtObservacao(request.dtObservacao() == null ? agora : request.dtObservacao())
				.dtCriadoEm(agora)
				.build();

		return toResponse(observacaoRepository.save(observacao));
	}

	@Transactional(readOnly = true)
	public ObservacaoClimaticaResponse buscarUltimaPorRegiao(Long idRegiao) {
		regiaoService.buscarAtiva(idRegiao);
		return observacaoRepository.findFirstByIdRegiaoOrderByDtObservacaoDescDtCriadoEmDesc(idRegiao)
				.map(this::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Nenhuma observação climática encontrada para a região: " + idRegiao));
	}

	private void validarMetricas(ObservacaoClimaticaCreateRequest request) {
		if (request.temperatura() == null
				&& request.umidade() == null
				&& request.precipitacao() == null
				&& request.vento() == null
				&& request.pressaoHpa() == null
				&& request.radiacaoSolar() == null
				&& request.indiceUv() == null) {
			throw new BusinessRuleException("Informe ao menos uma métrica climática");
		}
	}

	private ObservacaoClimaticaResponse toResponse(ObservacaoClimatica observacao) {
		return new ObservacaoClimaticaResponse(
				observacao.getIdObservacao(),
				observacao.getIdRegiao(),
				observacao.getFonte(),
				observacao.getTemperaturaCelsius(),
				observacao.getUmidadePercentual(),
				observacao.getPrecipitacaoMm(),
				observacao.getVentoKmh(),
				observacao.getPressaoHpa(),
				observacao.getRadiacaoSolar(),
				observacao.getIndiceUv(),
				observacao.getDtObservacao(),
				observacao.getDtCriadoEm());
	}

}
