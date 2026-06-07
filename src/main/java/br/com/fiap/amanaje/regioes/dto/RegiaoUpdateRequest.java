package br.com.fiap.amanaje.regioes.dto;

import java.math.BigDecimal;

import br.com.fiap.amanaje.regioes.enums.TipoArea;
import br.com.fiap.amanaje.regioes.enums.TipoVisibilidade;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RegiaoUpdateRequest(
		@NotNull @Positive Long idCliente,
		@NotBlank @Size(max = 150) String nome,
		@NotBlank @Size(max = 100) String cidade,
		@NotBlank @Pattern(regexp = "^[A-Z]{2}$") String estado,
		@NotNull @DecimalMin("-90") @DecimalMax("90") BigDecimal latitude,
		@NotNull @DecimalMin("-180") @DecimalMax("180") BigDecimal longitude,
		@NotNull TipoArea tipoArea,
		@NotNull @Min(0) @Max(100) Integer nivelVulnerabilidade,
		@NotNull TipoVisibilidade tipoVisibilidade) {
}
