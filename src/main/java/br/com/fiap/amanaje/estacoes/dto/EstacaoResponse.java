package br.com.fiap.amanaje.estacoes.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.fiap.amanaje.estacoes.enums.StatusEstacao;
import br.com.fiap.amanaje.estacoes.enums.TipoEstacao;

public record EstacaoResponse(
		Long idEstacao,
		Long idRegiao,
		String codigoEstacao,
		String nome,
		TipoEstacao tipoEstacao,
		StatusEstacao statusEstacao,
		BigDecimal latitude,
		BigDecimal longitude,
		LocalDateTime dtUltimaComunicacao,
		String stAtivo,
		LocalDateTime dtCriadoEm,
		LocalDateTime dtAtualizadoEm) {
}
