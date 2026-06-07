package br.com.fiap.amanaje.alertas.service;

import br.com.fiap.amanaje.alertas.enums.TipoAlerta;

import br.com.fiap.amanaje.alertas.enums.StatusAlerta;

import br.com.fiap.amanaje.alertas.repository.AlertaRepository;

import br.com.fiap.amanaje.alertas.model.Alerta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import br.com.fiap.amanaje.alertas.dto.AlertaResponse;
import br.com.fiap.amanaje.common.exception.ResourceNotFoundException;
import br.com.fiap.amanaje.riscos.model.AvaliacaoRisco;
import br.com.fiap.amanaje.riscos.enums.NivelRisco;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertaService {

	private static final String ATIVO = "S";
	private static final List<StatusAlerta> STATUS_EM_ANDAMENTO = List.of(
			StatusAlerta.ABERTO,
			StatusAlerta.EM_ANALISE);

	private final AlertaRepository alertaRepository;

	public AlertaService(AlertaRepository alertaRepository) {
		this.alertaRepository = alertaRepository;
	}

	@Transactional
	public Optional<AlertaResponse> gerarSeNecessario(AvaliacaoRisco avaliacao) {
		if (avaliacao.getNivelRisco() != NivelRisco.ALTO
				&& avaliacao.getNivelRisco() != NivelRisco.CRITICO) {
			return Optional.empty();
		}

		TipoAlerta tipoAlerta = TipoAlerta.valueOf(avaliacao.getTipoRisco().name());
		Optional<Alerta> alertaExistente = alertaRepository
				.findFirstByIdRegiaoAndTipoAlertaAndStatusAlertaInAndStAtivo(
						avaliacao.getIdRegiao(),
						tipoAlerta,
						STATUS_EM_ANDAMENTO,
						ATIVO);
		if (alertaExistente.isPresent()) {
			return alertaExistente.map(this::toResponse);
		}

		LocalDateTime agora = LocalDateTime.now();
		Alerta alerta = Alerta.builder()
				.idRegiao(avaliacao.getIdRegiao())
				.idAvaliacao(avaliacao.getIdAvaliacao())
				.tipoAlerta(tipoAlerta)
				.nivelRisco(avaliacao.getNivelRisco())
				.titulo(criarTitulo(avaliacao))
				.descricao("Score calculado: " + avaliacao.getScoreRisco() + ". " + avaliacao.getMotivo())
				.recomendacao(criarRecomendacao(tipoAlerta))
				.statusAlerta(StatusAlerta.ABERTO)
				.dtAlerta(agora)
				.stAtivo(ATIVO)
				.dtCriadoEm(agora)
				.build();

		return Optional.of(toResponse(alertaRepository.save(alerta)));
	}

	@Transactional(readOnly = true)
	public List<AlertaResponse> listarAtivos(
			Long idRegiao,
			StatusAlerta statusAlerta,
			NivelRisco nivelRisco) {
		return alertaRepository.findByStAtivo(ATIVO).stream()
				.filter(alerta -> idRegiao == null || alerta.getIdRegiao().equals(idRegiao))
				.filter(alerta -> statusAlerta == null || alerta.getStatusAlerta() == statusAlerta)
				.filter(alerta -> nivelRisco == null || alerta.getNivelRisco() == nivelRisco)
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public AlertaResponse resolver(Long idAlerta) {
		Alerta alerta = buscarAtivo(idAlerta);
		LocalDateTime agora = LocalDateTime.now();
		alerta.setStatusAlerta(StatusAlerta.RESOLVIDO);
		alerta.setDtResolvidoEm(agora);
		alerta.setDtAtualizadoEm(agora);
		return toResponse(alertaRepository.save(alerta));
	}

	private Alerta buscarAtivo(Long idAlerta) {
		return alertaRepository.findById(idAlerta)
				.filter(alerta -> ATIVO.equals(alerta.getStAtivo()))
				.orElseThrow(() -> new ResourceNotFoundException("Alerta ativo não encontrado: " + idAlerta));
	}

	private String criarTitulo(AvaliacaoRisco avaliacao) {
		return "Risco " + avaliacao.getNivelRisco().name().toLowerCase()
				+ " de " + avaliacao.getTipoRisco().name().toLowerCase().replace('_', ' ');
	}

	private String criarRecomendacao(TipoAlerta tipoAlerta) {
		return switch (tipoAlerta) {
			case ENCHENTE -> "Monitorar o nível da água e preparar medidas preventivas na região.";
			case DESLIZAMENTO -> "Inspecionar a área e restringir o acesso a pontos vulneráveis.";
			case TEMPESTADE -> "Acompanhar a previsão climática e orientar medidas de proteção.";
			case QUALIDADE_AR -> "Reduzir a exposição ao ar livre e monitorar os indicadores ambientais.";
			case OPERACIONAL -> "Verificar a operação da estação e acionar a equipe responsável.";
		};
	}

	private AlertaResponse toResponse(Alerta alerta) {
		return new AlertaResponse(
				alerta.getIdAlerta(),
				alerta.getIdRegiao(),
				alerta.getIdAvaliacao(),
				alerta.getTipoAlerta(),
				alerta.getNivelRisco(),
				alerta.getTitulo(),
				alerta.getDescricao(),
				alerta.getRecomendacao(),
				alerta.getStatusAlerta(),
				alerta.getDtAlerta(),
				alerta.getDtResolvidoEm(),
				alerta.getStAtivo(),
				alerta.getDtCriadoEm(),
				alerta.getDtAtualizadoEm());
	}

}
