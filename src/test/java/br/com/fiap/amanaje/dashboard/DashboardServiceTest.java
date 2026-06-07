package br.com.fiap.amanaje.dashboard;

import br.com.fiap.amanaje.dashboard.service.DashboardService;

import br.com.fiap.amanaje.alertas.repository.AlertaRepository;

import br.com.fiap.amanaje.riscos.repository.AvaliacaoRiscoRepository;

import br.com.fiap.amanaje.observacoes.repository.ObservacaoClimaticaRepository;

import br.com.fiap.amanaje.leituras.repository.LeituraIotRepository;

import br.com.fiap.amanaje.estacoes.repository.EstacaoIotRepository;

import br.com.fiap.amanaje.regioes.repository.RegiaoMonitoradaRepository;

import br.com.fiap.amanaje.clientes.repository.ClienteRepository;

import br.com.fiap.amanaje.clientes.service.ClienteService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.amanaje.alertas.model.Alerta;
import br.com.fiap.amanaje.alertas.repository.AlertaRepository;
import br.com.fiap.amanaje.alertas.enums.StatusAlerta;
import br.com.fiap.amanaje.clientes.model.Cliente;
import br.com.fiap.amanaje.clientes.repository.ClienteRepository;
import br.com.fiap.amanaje.clientes.service.ClienteService;
import br.com.fiap.amanaje.dashboard.dto.DashboardSummaryResponse;
import br.com.fiap.amanaje.estacoes.model.EstacaoIot;
import br.com.fiap.amanaje.estacoes.repository.EstacaoIotRepository;
import br.com.fiap.amanaje.leituras.model.LeituraIot;
import br.com.fiap.amanaje.leituras.repository.LeituraIotRepository;
import br.com.fiap.amanaje.observacoes.model.ObservacaoClimatica;
import br.com.fiap.amanaje.observacoes.repository.ObservacaoClimaticaRepository;
import br.com.fiap.amanaje.regioes.model.RegiaoMonitorada;
import br.com.fiap.amanaje.regioes.repository.RegiaoMonitoradaRepository;
import br.com.fiap.amanaje.riscos.model.AvaliacaoRisco;
import br.com.fiap.amanaje.riscos.repository.AvaliacaoRiscoRepository;
import br.com.fiap.amanaje.riscos.enums.NivelRisco;
import br.com.fiap.amanaje.riscos.enums.TipoRisco;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

	@Mock
	private ClienteRepository clienteRepository;

	@Mock
	private ClienteService clienteService;

	@Mock
	private RegiaoMonitoradaRepository regiaoRepository;

	@Mock
	private EstacaoIotRepository estacaoRepository;

	@Mock
	private LeituraIotRepository leituraRepository;

	@Mock
	private ObservacaoClimaticaRepository observacaoRepository;

	@Mock
	private AvaliacaoRiscoRepository avaliacaoRepository;

	@Mock
	private AlertaRepository alertaRepository;

	@InjectMocks
	private DashboardService dashboardService;

	@Test
	void shouldAggregateGlobalSummaryUsingActiveRegionsAndCurrentRisks() {
		LocalDateTime agora = LocalDateTime.now();
		when(clienteRepository.findByStAtivo("S")).thenReturn(List.of(
				Cliente.builder().idCliente(1L).build(),
				Cliente.builder().idCliente(2L).build()));
		when(regiaoRepository.findByStAtivo("S")).thenReturn(List.of(
				RegiaoMonitorada.builder().idRegiao(10L).build(),
				RegiaoMonitorada.builder().idRegiao(11L).build()));
		when(estacaoRepository.findByStAtivo("S")).thenReturn(List.of(
				EstacaoIot.builder().idRegiao(10L).build(),
				EstacaoIot.builder().idRegiao(99L).build()));
		when(alertaRepository.findByStAtivo("S")).thenReturn(List.of(
				alerta(10L, NivelRisco.CRITICO, StatusAlerta.ABERTO),
				alerta(11L, NivelRisco.ALTO, StatusAlerta.RESOLVIDO),
				alerta(99L, NivelRisco.CRITICO, StatusAlerta.ABERTO)));
		when(leituraRepository.findByStValida("S")).thenReturn(List.of(
				LeituraIot.builder().idRegiao(10L).build(),
				LeituraIot.builder().idRegiao(99L).build()));
		when(observacaoRepository.findAll()).thenReturn(List.of(
				ObservacaoClimatica.builder().idRegiao(11L).build(),
				ObservacaoClimatica.builder().idRegiao(99L).build()));
		when(avaliacaoRepository.findAll()).thenReturn(List.of(
				avaliacao(10L, TipoRisco.ENCHENTE, NivelRisco.CRITICO, agora.minusMinutes(2)),
				avaliacao(10L, TipoRisco.ENCHENTE, NivelRisco.ALTO, agora.minusMinutes(1)),
				avaliacao(11L, TipoRisco.TEMPESTADE, NivelRisco.BAIXO, agora),
				avaliacao(99L, TipoRisco.QUALIDADE_AR, NivelRisco.CRITICO, agora)));

		DashboardSummaryResponse response = dashboardService.buscarResumo(null);

		assertThat(response.totalClientesAtivos()).isEqualTo(2);
		assertThat(response.totalRegioesAtivas()).isEqualTo(2);
		assertThat(response.totalEstacoesAtivas()).isEqualTo(1);
		assertThat(response.totalAlertasAtivos()).isEqualTo(2);
		assertThat(response.totalAlertasCriticos()).isEqualTo(1);
		assertThat(response.totalAlertasAltos()).isEqualTo(1);
		assertThat(response.totalAlertasResolvidos()).isEqualTo(1);
		assertThat(response.totalLeiturasValidas()).isEqualTo(1);
		assertThat(response.totalObservacoesClimaticas()).isEqualTo(1);
		assertThat(response.totalAvaliacoesRisco()).isEqualTo(3);
		assertThat(response.regioesComRiscoAltoOuCritico()).isEqualTo(1);
		assertThat(response.maiorNivelRiscoAtual()).isEqualTo(NivelRisco.ALTO);
		assertThat(response.atualizadoEm()).isNotNull();
	}

	@Test
	void shouldValidateClientAndReturnScopedEmptySummary() {
		when(regiaoRepository.findByIdClienteAndStAtivo(8L, "S")).thenReturn(List.of());
		when(estacaoRepository.findByStAtivo("S")).thenReturn(List.of());
		when(alertaRepository.findByStAtivo("S")).thenReturn(List.of());
		when(leituraRepository.findByStValida("S")).thenReturn(List.of());
		when(observacaoRepository.findAll()).thenReturn(List.of());
		when(avaliacaoRepository.findAll()).thenReturn(List.of());

		DashboardSummaryResponse response = dashboardService.buscarResumo(8L);

		verify(clienteService).buscarAtivo(8L);
		assertThat(response.totalClientesAtivos()).isEqualTo(1);
		assertThat(response.totalRegioesAtivas()).isZero();
		assertThat(response.maiorNivelRiscoAtual()).isNull();
	}

	private Alerta alerta(Long idRegiao, NivelRisco nivelRisco, StatusAlerta statusAlerta) {
		return Alerta.builder()
				.idRegiao(idRegiao)
				.nivelRisco(nivelRisco)
				.statusAlerta(statusAlerta)
				.build();
	}

	private AvaliacaoRisco avaliacao(
			Long idRegiao,
			TipoRisco tipoRisco,
			NivelRisco nivelRisco,
			LocalDateTime dtAvaliacao) {
		return AvaliacaoRisco.builder()
				.idRegiao(idRegiao)
				.tipoRisco(tipoRisco)
				.nivelRisco(nivelRisco)
				.dtAvaliacao(dtAvaliacao)
				.build();
	}

}
