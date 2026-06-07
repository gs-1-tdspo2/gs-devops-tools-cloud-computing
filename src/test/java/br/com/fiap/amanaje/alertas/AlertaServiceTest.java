package br.com.fiap.amanaje.alertas;

import br.com.fiap.amanaje.alertas.enums.TipoAlerta;

import br.com.fiap.amanaje.alertas.enums.StatusAlerta;

import br.com.fiap.amanaje.alertas.repository.AlertaRepository;

import br.com.fiap.amanaje.alertas.service.AlertaService;

import br.com.fiap.amanaje.alertas.model.Alerta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import br.com.fiap.amanaje.alertas.dto.AlertaResponse;
import br.com.fiap.amanaje.riscos.model.AvaliacaoRisco;
import br.com.fiap.amanaje.riscos.enums.NivelRisco;
import br.com.fiap.amanaje.riscos.enums.TipoRisco;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

	@Mock
	private AlertaRepository alertaRepository;

	@InjectMocks
	private AlertaService alertaService;

	@Test
	void shouldCreateOpenAlertForHighRisk() {
		AvaliacaoRisco avaliacao = AvaliacaoRisco.builder()
				.idAvaliacao(10L)
				.idRegiao(20L)
				.tipoRisco(TipoRisco.DESLIZAMENTO)
				.nivelRisco(NivelRisco.ALTO)
				.scoreRisco(BigDecimal.valueOf(70))
				.motivo("Inclinação elevada.")
				.build();
		when(alertaRepository.findFirstByIdRegiaoAndTipoAlertaAndStatusAlertaInAndStAtivo(
				20L,
				TipoAlerta.DESLIZAMENTO,
				java.util.List.of(StatusAlerta.ABERTO, StatusAlerta.EM_ANALISE),
				"S"))
				.thenReturn(Optional.empty());
		when(alertaRepository.save(any(Alerta.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		AlertaResponse response = alertaService.gerarSeNecessario(avaliacao).orElseThrow();

		assertThat(response.tipoAlerta()).isEqualTo(TipoAlerta.DESLIZAMENTO);
		assertThat(response.nivelRisco()).isEqualTo(NivelRisco.ALTO);
		assertThat(response.statusAlerta()).isEqualTo(StatusAlerta.ABERTO);
		assertThat(response.stAtivo()).isEqualTo("S");
		assertThat(response.dtAlerta()).isNotNull();
		assertThat(response.dtCriadoEm()).isNotNull();
	}

	@Test
	void shouldReuseExistingActiveAlertForSameRegionAndRiskType() {
		AvaliacaoRisco avaliacao = AvaliacaoRisco.builder()
				.idAvaliacao(10L)
				.idRegiao(20L)
				.tipoRisco(TipoRisco.ENCHENTE)
				.nivelRisco(NivelRisco.CRITICO)
				.scoreRisco(BigDecimal.valueOf(90))
				.motivo("Nível da água elevado.")
				.build();
		Alerta existente = Alerta.builder()
				.idAlerta(30L)
				.idRegiao(20L)
				.idAvaliacao(9L)
				.tipoAlerta(TipoAlerta.ENCHENTE)
				.nivelRisco(NivelRisco.ALTO)
				.statusAlerta(StatusAlerta.ABERTO)
				.stAtivo("S")
				.build();
		when(alertaRepository.findFirstByIdRegiaoAndTipoAlertaAndStatusAlertaInAndStAtivo(
				20L,
				TipoAlerta.ENCHENTE,
				java.util.List.of(StatusAlerta.ABERTO, StatusAlerta.EM_ANALISE),
				"S"))
				.thenReturn(Optional.of(existente));

		Optional<AlertaResponse> response = alertaService.gerarSeNecessario(avaliacao);

		assertThat(response).isPresent();
		assertThat(response.orElseThrow().idAlerta()).isEqualTo(30L);
		verify(alertaRepository, never()).save(any(Alerta.class));
	}

	@Test
	void shouldResolveActiveAlert() {
		Alerta alerta = Alerta.builder()
				.idAlerta(30L)
				.statusAlerta(StatusAlerta.ABERTO)
				.stAtivo("S")
				.build();
		when(alertaRepository.findById(30L)).thenReturn(Optional.of(alerta));
		when(alertaRepository.save(alerta)).thenReturn(alerta);

		AlertaResponse response = alertaService.resolver(30L);

		assertThat(response.statusAlerta()).isEqualTo(StatusAlerta.RESOLVIDO);
		assertThat(response.dtResolvidoEm()).isNotNull();
		assertThat(response.dtAtualizadoEm()).isNotNull();
	}

}
