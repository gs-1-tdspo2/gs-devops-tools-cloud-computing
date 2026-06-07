package br.com.fiap.amanaje.riscos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.fiap.amanaje.riscos.enums.NivelRisco;
import br.com.fiap.amanaje.riscos.enums.TipoRisco;

public record AvaliacaoRiscoResponse(
		Long idAvaliacao,
		Long idRegiao,
		Long idLeitura,
		Long idObservacao,
		TipoRisco tipoRisco,
		BigDecimal scoreRisco,
		NivelRisco nivelRisco,
		String motivo,
		LocalDateTime dtAvaliacao) {
}
