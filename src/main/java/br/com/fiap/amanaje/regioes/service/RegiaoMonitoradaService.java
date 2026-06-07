package br.com.fiap.amanaje.regioes.service;

import br.com.fiap.amanaje.regioes.enums.TipoVisibilidade;

import br.com.fiap.amanaje.regioes.enums.TipoArea;

import br.com.fiap.amanaje.regioes.repository.RegiaoMonitoradaRepository;

import br.com.fiap.amanaje.regioes.model.RegiaoMonitorada;

import br.com.fiap.amanaje.clientes.service.ClienteService;

import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.amanaje.clientes.service.ClienteService;
import br.com.fiap.amanaje.common.exception.ResourceNotFoundException;
import br.com.fiap.amanaje.regioes.dto.RegiaoCreateRequest;
import br.com.fiap.amanaje.regioes.dto.RegiaoResponse;
import br.com.fiap.amanaje.regioes.dto.RegiaoUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegiaoMonitoradaService {

	private static final String ATIVO = "S";
	private static final String INATIVO = "N";

	private final RegiaoMonitoradaRepository regiaoRepository;
	private final ClienteService clienteService;

	public RegiaoMonitoradaService(
			RegiaoMonitoradaRepository regiaoRepository,
			ClienteService clienteService) {
		this.regiaoRepository = regiaoRepository;
		this.clienteService = clienteService;
	}

	@Transactional
	public RegiaoResponse criar(RegiaoCreateRequest request) {
		clienteService.buscarAtivo(request.idCliente());
		RegiaoMonitorada regiao = RegiaoMonitorada.builder()
				.idCliente(request.idCliente())
				.nome(request.nome())
				.cidade(request.cidade())
				.estado(request.estado())
				.latitude(request.latitude())
				.longitude(request.longitude())
				.tipoArea(request.tipoArea())
				.nivelVulnerabilidade(request.nivelVulnerabilidade())
				.tipoVisibilidade(request.tipoVisibilidade())
				.stAtivo(ATIVO)
				.dtCriadoEm(LocalDateTime.now())
				.build();

		return toResponse(regiaoRepository.save(regiao));
	}

	@Transactional(readOnly = true)
	public List<RegiaoResponse> listarAtivas(
			Long idCliente,
			String estado,
			String cidade,
			TipoVisibilidade tipoVisibilidade) {
		List<RegiaoMonitorada> regioes = idCliente == null
				? regiaoRepository.findByStAtivo(ATIVO)
				: regiaoRepository.findByIdClienteAndStAtivo(idCliente, ATIVO);

		return regioes.stream()
				.filter(regiao -> estado == null || regiao.getEstado().equalsIgnoreCase(estado))
				.filter(regiao -> cidade == null || regiao.getCidade().equalsIgnoreCase(cidade))
				.filter(regiao -> tipoVisibilidade == null || regiao.getTipoVisibilidade() == tipoVisibilidade)
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public RegiaoResponse buscarPorId(Long idRegiao) {
		return toResponse(buscarAtiva(idRegiao));
	}

	@Transactional
	public RegiaoResponse atualizar(Long idRegiao, RegiaoUpdateRequest request) {
		clienteService.buscarAtivo(request.idCliente());
		RegiaoMonitorada regiao = buscarAtiva(idRegiao);
		regiao.setIdCliente(request.idCliente());
		regiao.setNome(request.nome());
		regiao.setCidade(request.cidade());
		regiao.setEstado(request.estado());
		regiao.setLatitude(request.latitude());
		regiao.setLongitude(request.longitude());
		regiao.setTipoArea(request.tipoArea());
		regiao.setNivelVulnerabilidade(request.nivelVulnerabilidade());
		regiao.setTipoVisibilidade(request.tipoVisibilidade());
		regiao.setDtAtualizadoEm(LocalDateTime.now());

		return toResponse(regiaoRepository.save(regiao));
	}

	@Transactional
	public void inativar(Long idRegiao) {
		RegiaoMonitorada regiao = buscarAtiva(idRegiao);
		LocalDateTime agora = LocalDateTime.now();
		regiao.setStAtivo(INATIVO);
		regiao.setDtDelEm(agora);
		regiao.setDtAtualizadoEm(agora);
		regiaoRepository.save(regiao);
	}

	@Transactional(readOnly = true)
	public RegiaoMonitorada buscarAtiva(Long idRegiao) {
		return regiaoRepository.findById(idRegiao)
				.filter(regiao -> ATIVO.equals(regiao.getStAtivo()))
				.orElseThrow(() -> new ResourceNotFoundException("Região monitorada ativa não encontrada: " + idRegiao));
	}

	private RegiaoResponse toResponse(RegiaoMonitorada regiao) {
		return new RegiaoResponse(
				regiao.getIdRegiao(),
				regiao.getIdCliente(),
				regiao.getNome(),
				regiao.getCidade(),
				regiao.getEstado(),
				regiao.getLatitude(),
				regiao.getLongitude(),
				regiao.getTipoArea(),
				regiao.getNivelVulnerabilidade(),
				regiao.getTipoVisibilidade(),
				regiao.getStAtivo(),
				regiao.getDtCriadoEm(),
				regiao.getDtAtualizadoEm());
	}

}
