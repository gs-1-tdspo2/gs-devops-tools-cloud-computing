package br.com.fiap.amanaje.riscos.model;

import br.com.fiap.amanaje.riscos.enums.TipoRisco;

import br.com.fiap.amanaje.riscos.enums.NivelRisco;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name = "TB_AMANAJE_AVAL_RISCO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvaliacaoRisco {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_AVALIACAO")
	private Long idAvaliacao;

	@Column(name = "ID_REGIAO")
	private Long idRegiao;

	@Column(name = "ID_LEITURA")
	private Long idLeitura;

	@Column(name = "ID_OBSERVACAO")
	private Long idObservacao;

	@Enumerated(EnumType.STRING)
	@Column(name = "TP_RISCO")
	private TipoRisco tipoRisco;

	@Column(name = "NR_SCORE_RISCO")
	private BigDecimal scoreRisco;

	@Enumerated(EnumType.STRING)
	@Column(name = "TP_NIVEL_RISCO")
	private NivelRisco nivelRisco;

	@Column(name = "DS_MOTIVO")
	private String motivo;

	@Column(name = "DT_AVAL")
	private LocalDateTime dtAvaliacao;

}
