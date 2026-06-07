package br.com.fiap.amanaje.observacoes.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ObservacaoClimaticaCreateRequest(
		@NotNull @Positive Long idRegiao,
		@NotBlank @Size(max = 80) String fonte,
		BigDecimal temperatura,
		@DecimalMin("0") @DecimalMax("100") BigDecimal umidade,
		@PositiveOrZero BigDecimal precipitacao,
		@PositiveOrZero BigDecimal vento,
		@DecimalMin("800") @DecimalMax("1200") BigDecimal pressaoHpa,
		BigDecimal radiacaoSolar,
		@DecimalMin("0") @DecimalMax("20") BigDecimal indiceUv,
		LocalDateTime dtObservacao) {
}
