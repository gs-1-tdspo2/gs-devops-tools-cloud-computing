package br.com.fiap.amanaje.observacoes.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_AMANAJE_OBS_CLIM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacaoClimatica {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_OBSERVACAO")
	private Long idObservacao;

	@Column(name = "ID_REGIAO")
	private Long idRegiao;

	@Column(name = "NM_FONTE")
	private String fonte;

	@Column(name = "NR_TEMPERATURA_C")
	private BigDecimal temperaturaCelsius;

	@Column(name = "NR_UMIDADE_PCT")
	private BigDecimal umidadePercentual;

	@Column(name = "NR_PRECIP_MM")
	private BigDecimal precipitacaoMm;

	@Column(name = "NR_VENTO_KMH")
	private BigDecimal ventoKmh;

	@Column(name = "NR_PRESSAO_HPA")
	private BigDecimal pressaoHpa;

	@Column(name = "NR_RADIACAO_SOLAR")
	private BigDecimal radiacaoSolar;

	@Column(name = "NR_INDICE_UV")
	private BigDecimal indiceUv;

	@Column(name = "DT_OBS")
	private LocalDateTime dtObservacao;

	@Column(name = "DT_CRIADO_EM")
	private LocalDateTime dtCriadoEm;

}
