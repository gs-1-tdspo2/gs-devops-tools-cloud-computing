package br.com.fiap.amanaje.dashboard.service;

import br.com.fiap.amanaje.alertas.repository.AlertaRepository;

import br.com.fiap.amanaje.riscos.repository.AvaliacaoRiscoRepository;

import br.com.fiap.amanaje.observacoes.repository.ObservacaoClimaticaRepository;

import br.com.fiap.amanaje.leituras.repository.LeituraIotRepository;

import br.com.fiap.amanaje.estacoes.repository.EstacaoIotRepository;

import br.com.fiap.amanaje.regioes.repository.RegiaoMonitoradaRepository;

import br.com.fiap.amanaje.clientes.repository.ClienteRepository;

import br.com.fiap.amanaje.clientes.service.ClienteService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import br.com.fiap.amanaje.alertas.model.Alerta;
import br.com.fiap.amanaje.alertas.repository.AlertaRepository;
import br.com.fiap.amanaje.alertas.enums.StatusAlerta;
import br.com.fiap.amanaje.clientes.repository.ClienteRepository;
import br.com.fiap.amanaje.clientes.service.ClienteService;
import br.com.fiap.amanaje.dashboard.dto.DashboardSummaryResponse;
import br.com.fiap.amanaje.estacoes.repository.EstacaoIotRepository;
import br.com.fiap.amanaje.leituras.repository.LeituraIotRepository;
import br.com.fiap.amanaje.observacoes.repository.ObservacaoClimaticaRepository;
import br.com.fiap.amanaje.regioes.model.RegiaoMonitorada;
import br.com.fiap.amanaje.regioes.repository.RegiaoMonitoradaRepository;
import br.com.fiap.amanaje.riscos.model.AvaliacaoRisco;
import br.com.fiap.amanaje.riscos.repository.AvaliacaoRiscoRepository;
import br.com.fiap.amanaje.riscos.enums.NivelRisco;
import br.com.fiap.amanaje.riscos.enums.TipoRisco;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

	private static final String ATIVO = "S";
	private static final String VALIDA = "S";

	private final ClienteRepository clienteRepository;
	private final ClienteService clienteService;
	private final RegiaoMonitoradaRepository regiaoRepository;
	private final EstacaoIotRepository estacaoRepository;
	private final LeituraIotRepository leituraRepository;
	private final ObservacaoClimaticaRepository observacaoRepository;
	private final AvaliacaoRiscoRepository avaliacaoRepository;
	private final AlertaRepository alertaRepository;

	public DashboardService(
			ClienteRepository clienteRepository,
			ClienteService clienteService,
			RegiaoMonitoradaRepository regiaoRepository,
			EstacaoIotRepository estacaoRepository,
			LeituraIotRepository leituraRepository,
			ObservacaoClimaticaRepository observacaoRepository,
			AvaliacaoRiscoRepository avaliacaoRepository,
			AlertaRepository alertaRepository) {
		this.clienteRepository = clienteRepository;
		this.clienteService = clienteService;
		this.regiaoRepository = regiaoRepository;
		this.estacaoRepository = estacaoRepository;
		this.leituraRepository = leituraRepository;
		this.observacaoRepository = observacaoRepository;
		this.avaliacaoRepository = avaliacaoRepository;
		this.alertaRepository = alertaRepository;
	}

	@Transactional(readOnly = true)
	public DashboardSummaryResponse buscarResumo(Long idCliente) {
		List<RegiaoMonitorada> regioesAtivas = buscarRegioesAtivas(idCliente);
		Set<Long> idsRegioesAtivas = regioesAtivas.stream()
				.map(RegiaoMonitorada::getIdRegiao)
				.collect(Collectors.toSet());
		List<Alerta> alertasAtivos = alertaRepository.findByStAtivo(ATIVO).stream()
				.filter(alerta -> idsRegioesAtivas.contains(alerta.getIdRegiao()))
				.toList();
		List<AvaliacaoRisco> avaliacoes = avaliacaoRepository.findAll().stream()
				.filter(avaliacao -> idsRegioesAtivas.contains(avaliacao.getIdRegiao()))
				.toList();
		List<AvaliacaoRisco> riscosAtuais = buscarRiscosAtuais(avaliacoes);

		return new DashboardSummaryResponse(
				idCliente == null ? clienteRepository.findByStAtivo(ATIVO).size() : 1,
				regioesAtivas.size(),
				estacaoRepository.findByStAtivo(ATIVO).stream()
						.filter(estacao -> idsRegioesAtivas.contains(estacao.getIdRegiao()))
						.count(),
				alertasAtivos.size(),
				contarAlertasPorNivel(alertasAtivos, NivelRisco.CRITICO),
				contarAlertasPorNivel(alertasAtivos, NivelRisco.ALTO),
				alertasAtivos.stream()
						.filter(alerta -> StatusAlerta.RESOLVIDO == alerta.getStatusAlerta())
						.count(),
				leituraRepository.findByStValida(VALIDA).stream()
						.filter(leitura -> idsRegioesAtivas.contains(leitura.getIdRegiao()))
						.count(),
				observacaoRepository.findAll().stream()
						.filter(observacao -> idsRegioesAtivas.contains(observacao.getIdRegiao()))
						.count(),
				avaliacoes.size(),
				riscosAtuais.stream()
						.filter(avaliacao -> isAltoOuCritico(avaliacao.getNivelRisco()))
						.map(AvaliacaoRisco::getIdRegiao)
						.distinct()
						.count(),
				riscosAtuais.stream()
						.map(AvaliacaoRisco::getNivelRisco)
						.max(Comparator.comparingInt(this::ordemNivel))
						.orElse(null),
				LocalDateTime.now());
	}

	private List<RegiaoMonitorada> buscarRegioesAtivas(Long idCliente) {
		if (idCliente == null) {
			return regiaoRepository.findByStAtivo(ATIVO);
		}
		clienteService.buscarAtivo(idCliente);
		return regiaoRepository.findByIdClienteAndStAtivo(idCliente, ATIVO);
	}

	private List<AvaliacaoRisco> buscarRiscosAtuais(List<AvaliacaoRisco> avaliacoes) {
		Map<RiscoAtualKey, AvaliacaoRisco> atuais = avaliacoes.stream()
				.collect(Collectors.toMap(
						avaliacao -> new RiscoAtualKey(avaliacao.getIdRegiao(), avaliacao.getTipoRisco()),
						Function.identity(),
						this::maisRecente));
		return atuais.values().stream().toList();
	}

	private AvaliacaoRisco maisRecente(AvaliacaoRisco atual, AvaliacaoRisco candidata) {
		if (atual.getDtAvaliacao() == null) {
			return candidata;
		}
		if (candidata.getDtAvaliacao() == null) {
			return atual;
		}
		return candidata.getDtAvaliacao().isAfter(atual.getDtAvaliacao()) ? candidata : atual;
	}

	private long contarAlertasPorNivel(List<Alerta> alertas, NivelRisco nivelRisco) {
		return alertas.stream()
				.filter(alerta -> nivelRisco == alerta.getNivelRisco())
				.count();
	}

	private boolean isAltoOuCritico(NivelRisco nivelRisco) {
		return NivelRisco.ALTO == nivelRisco || NivelRisco.CRITICO == nivelRisco;
	}

	private int ordemNivel(NivelRisco nivelRisco) {
		return nivelRisco == null ? -1 : nivelRisco.ordinal();
	}

	private record RiscoAtualKey(Long idRegiao, TipoRisco tipoRisco) {
	}

}
