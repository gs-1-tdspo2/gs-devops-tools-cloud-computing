package br.com.fiap.amanaje.estacoes.model;

import br.com.fiap.amanaje.estacoes.enums.TipoEstacao;

import br.com.fiap.amanaje.estacoes.enums.StatusEstacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.fiap.amanaje.common.model.EntidadeAuditavel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "TB_AMANAJE_EST_IOT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EstacaoIot extends EntidadeAuditavel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_ESTACAO")
	private Long idEstacao;

	@Column(name = "ID_REGIAO")
	private Long idRegiao;

	@Column(name = "CD_EST")
	private String codigoEstacao;

	@Column(name = "NM_EST")
	private String nome;

	@Enumerated(EnumType.STRING)
	@Column(name = "TP_EST")
	private TipoEstacao tipoEstacao;

	@Enumerated(EnumType.STRING)
	@Column(name = "ST_EST")
	private StatusEstacao statusEstacao;

	@Column(name = "NR_LATITUDE")
	private BigDecimal latitude;

	@Column(name = "NR_LONGITUDE")
	private BigDecimal longitude;

	@Column(name = "DT_ULTIMA_COM")
	private LocalDateTime dtUltimaComunicacao;

}
