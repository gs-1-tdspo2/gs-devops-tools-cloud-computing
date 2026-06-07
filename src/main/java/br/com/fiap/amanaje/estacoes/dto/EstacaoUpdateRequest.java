package br.com.fiap.amanaje.estacoes.dto;

import java.math.BigDecimal;

import br.com.fiap.amanaje.estacoes.enums.StatusEstacao;
import br.com.fiap.amanaje.estacoes.enums.TipoEstacao;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EstacaoUpdateRequest(
		@NotNull @Positive Long idRegiao,
		@NotBlank @Size(max = 40) String codigoEstacao,
		@NotBlank @Size(max = 120) String nome,
		@NotNull TipoEstacao tipoEstacao,
		@NotNull StatusEstacao statusEstacao,
		@DecimalMin("-90") @DecimalMax("90") BigDecimal latitude,
		@DecimalMin("-180") @DecimalMax("180") BigDecimal longitude) {
}
