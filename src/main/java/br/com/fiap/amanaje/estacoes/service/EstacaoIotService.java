package br.com.fiap.amanaje.estacoes.service;

import br.com.fiap.amanaje.estacoes.enums.TipoEstacao;

import br.com.fiap.amanaje.estacoes.enums.StatusEstacao;

import br.com.fiap.amanaje.estacoes.repository.EstacaoIotRepository;

import br.com.fiap.amanaje.estacoes.model.EstacaoIot;

import br.com.fiap.amanaje.regioes.service.RegiaoMonitoradaService;

import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.amanaje.common.exception.BusinessRuleException;
import br.com.fiap.amanaje.common.exception.ResourceNotFoundException;
import br.com.fiap.amanaje.estacoes.dto.EstacaoCreateRequest;
import br.com.fiap.amanaje.estacoes.dto.EstacaoResponse;
import br.com.fiap.amanaje.estacoes.dto.EstacaoUpdateRequest;
import br.com.fiap.amanaje.regioes.service.RegiaoMonitoradaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstacaoIotService {

	private static final String ATIVO = "S";
	private static final String INATIVO = "N";

	private final EstacaoIotRepository estacaoRepository;
	private final RegiaoMonitoradaService regiaoService;

	public EstacaoIotService(
			EstacaoIotRepository estacaoRepository,
			RegiaoMonitoradaService regiaoService) {
		this.estacaoRepository = estacaoRepository;
		this.regiaoService = regiaoService;
	}

	@Transactional
	public EstacaoResponse criar(EstacaoCreateRequest request) {
		regiaoService.buscarAtiva(request.idRegiao());
		validarCodigoUnico(request.codigoEstacao(), null);
		EstacaoIot estacao = EstacaoIot.builder()
				.idRegiao(request.idRegiao())
				.codigoEstacao(request.codigoEstacao())
				.nome(request.nome())
				.tipoEstacao(request.tipoEstacao())
				.statusEstacao(request.statusEstacao())
				.latitude(request.latitude())
				.longitude(request.longitude())
				.stAtivo(ATIVO)
				.dtCriadoEm(LocalDateTime.now())
				.build();

		return toResponse(estacaoRepository.save(estacao));
	}

	@Transactional(readOnly = true)
	public List<EstacaoResponse> listarAtivasPorRegiao(Long idRegiao) {
		regiaoService.buscarAtiva(idRegiao);
		return estacaoRepository.findByIdRegiaoAndStAtivo(idRegiao, ATIVO).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public EstacaoResponse buscarPorId(Long idEstacao) {
		return toResponse(buscarAtiva(idEstacao));
	}

	@Transactional
	public EstacaoResponse atualizar(Long idEstacao, EstacaoUpdateRequest request) {
		regiaoService.buscarAtiva(request.idRegiao());
		EstacaoIot estacao = buscarAtiva(idEstacao);
		validarCodigoUnico(request.codigoEstacao(), idEstacao);
		estacao.setIdRegiao(request.idRegiao());
		estacao.setCodigoEstacao(request.codigoEstacao());
		estacao.setNome(request.nome());
		estacao.setTipoEstacao(request.tipoEstacao());
		estacao.setStatusEstacao(request.statusEstacao());
		estacao.setLatitude(request.latitude());
		estacao.setLongitude(request.longitude());
		estacao.setDtAtualizadoEm(LocalDateTime.now());

		return toResponse(estacaoRepository.save(estacao));
	}

	@Transactional
	public void inativar(Long idEstacao) {
		EstacaoIot estacao = buscarAtiva(idEstacao);
		LocalDateTime agora = LocalDateTime.now();
		estacao.setStAtivo(INATIVO);
		estacao.setDtDelEm(agora);
		estacao.setDtAtualizadoEm(agora);
		estacaoRepository.save(estacao);
	}

	@Transactional(readOnly = true)
	public EstacaoIot buscarAtiva(Long idEstacao) {
		return estacaoRepository.findById(idEstacao)
				.filter(estacao -> ATIVO.equals(estacao.getStAtivo()))
				.orElseThrow(() -> new ResourceNotFoundException("Estação IoT ativa não encontrada: " + idEstacao));
	}

	@Transactional(readOnly = true)
	public EstacaoIot buscarAtivaPorCodigo(String codigoEstacao) {
		return estacaoRepository.findByCodigoEstacao(codigoEstacao)
				.filter(estacao -> ATIVO.equals(estacao.getStAtivo()))
				.orElseThrow(() -> new ResourceNotFoundException(
						"Estação IoT ativa não encontrada para o código: " + codigoEstacao));
	}

	private void validarCodigoUnico(String codigoEstacao, Long idEstacaoAtual) {
		estacaoRepository.findByCodigoEstacao(codigoEstacao)
				.filter(estacao -> !estacao.getIdEstacao().equals(idEstacaoAtual))
				.ifPresent(estacao -> {
					throw new BusinessRuleException("Código de estação já cadastrado: " + codigoEstacao);
				});
	}

	private EstacaoResponse toResponse(EstacaoIot estacao) {
		return new EstacaoResponse(
				estacao.getIdEstacao(),
				estacao.getIdRegiao(),
				estacao.getCodigoEstacao(),
				estacao.getNome(),
				estacao.getTipoEstacao(),
				estacao.getStatusEstacao(),
				estacao.getLatitude(),
				estacao.getLongitude(),
				estacao.getDtUltimaComunicacao(),
				estacao.getStAtivo(),
				estacao.getDtCriadoEm(),
				estacao.getDtAtualizadoEm());
	}

}
