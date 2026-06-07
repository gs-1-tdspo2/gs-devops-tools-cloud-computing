package br.com.fiap.amanaje.indicadores.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.fiap.amanaje.riscos.enums.NivelRisco;
import br.com.fiap.amanaje.riscos.enums.TipoRisco;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "TB_AMANAJE_IND_REG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndicadorRegional {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_INDICADOR")
	private Long idIndicador;

	@Column(name = "ID_REGIAO")
	private Long idRegiao;

	@Column(name = "SG_ESTADO", length = 2, columnDefinition = "CHAR(2)")
	private String estado;

	@Column(name = "NM_CIDADE")
	private String cidade;

	@Column(name = "NM_REGIAO")
	private String nomeRegiao;

	@Enumerated(EnumType.STRING)
	@Column(name = "TP_RISCO")
	private TipoRisco tipoRisco;

	@Column(name = "NR_SCORE_MEDIO")
	private BigDecimal scoreMedio;

	@Enumerated(EnumType.STRING)
	@Column(name = "TP_NIVEL_RISCO_MEDIO")
	private NivelRisco nivelRiscoMedio;

	@Column(name = "QT_ESTACOES")
	private Integer quantidadeEstacoes;

	@Column(name = "QT_ALERTAS_ATIVOS")
	private Integer quantidadeAlertasAtivos;

	@Column(name = "NM_FONTE_CALCULO")
	private String fonteCalculo;

	@Column(name = "DT_CALCULO")
	private LocalDateTime dtCalculo;

}
