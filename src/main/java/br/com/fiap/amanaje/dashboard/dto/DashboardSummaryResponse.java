package br.com.fiap.amanaje.dashboard.dto;

import java.time.LocalDateTime;

import br.com.fiap.amanaje.riscos.enums.NivelRisco;

public record DashboardSummaryResponse(
		long totalClientesAtivos,
		long totalRegioesAtivas,
		long totalEstacoesAtivas,
		long totalAlertasAtivos,
		long totalAlertasCriticos,
		long totalAlertasAltos,
		long totalAlertasResolvidos,
		long totalLeiturasValidas,
		long totalObservacoesClimaticas,
		long totalAvaliacoesRisco,
		long regioesComRiscoAltoOuCritico,
		NivelRisco maiorNivelRiscoAtual,
		LocalDateTime atualizadoEm) {
}
