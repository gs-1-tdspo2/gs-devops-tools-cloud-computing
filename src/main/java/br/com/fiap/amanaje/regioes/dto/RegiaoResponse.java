package br.com.fiap.amanaje.regioes.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.fiap.amanaje.regioes.enums.TipoArea;
import br.com.fiap.amanaje.regioes.enums.TipoVisibilidade;

public record RegiaoResponse(
		Long idRegiao,
		Long idCliente,
		String nome,
		String cidade,
		String estado,
		BigDecimal latitude,
		BigDecimal longitude,
		TipoArea tipoArea,
		Integer nivelVulnerabilidade,
		TipoVisibilidade tipoVisibilidade,
		String stAtivo,
		LocalDateTime dtCriadoEm,
		LocalDateTime dtAtualizadoEm) {
}
