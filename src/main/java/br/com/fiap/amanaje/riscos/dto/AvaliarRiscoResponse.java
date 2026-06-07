package br.com.fiap.amanaje.riscos.dto;

import java.util.List;

import br.com.fiap.amanaje.alertas.dto.AlertaResponse;

public record AvaliarRiscoResponse(
		Long idRegiao,
		List<AvaliacaoRiscoResponse> avaliacoes,
		List<AlertaResponse> alertas) {
}
