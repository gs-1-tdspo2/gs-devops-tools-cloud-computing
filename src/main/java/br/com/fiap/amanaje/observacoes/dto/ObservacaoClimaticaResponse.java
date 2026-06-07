package br.com.fiap.amanaje.observacoes.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ObservacaoClimaticaResponse(
		Long idObservacao,
		Long idRegiao,
		String fonte,
		BigDecimal temperatura,
		BigDecimal umidade,
		BigDecimal precipitacao,
		BigDecimal vento,
		BigDecimal pressaoHpa,
		BigDecimal radiacaoSolar,
		BigDecimal indiceUv,
		LocalDateTime dtObservacao,
		LocalDateTime dtCriadoEm) {
}
