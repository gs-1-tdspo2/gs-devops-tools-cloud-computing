package br.com.fiap.amanaje.regioes.model;

import br.com.fiap.amanaje.regioes.enums.TipoVisibilidade;

import br.com.fiap.amanaje.regioes.enums.TipoArea;

import java.math.BigDecimal;

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
@Table(name = "TB_AMANAJE_REGIAO_MONIT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RegiaoMonitorada extends EntidadeAuditavel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_REGIAO")
	private Long idRegiao;

	@Column(name = "ID_CLIENTE")
	private Long idCliente;

	@Column(name = "NM_REGIAO")
	private String nome;

	@Column(name = "NM_CIDADE")
	private String cidade;

	@Column(name = "SG_ESTADO", length = 2, columnDefinition = "CHAR(2)")
	private String estado;

	@Column(name = "NR_LATITUDE")
	private BigDecimal latitude;

	@Column(name = "NR_LONGITUDE")
	private BigDecimal longitude;

	@Enumerated(EnumType.STRING)
	@Column(name = "TP_AREA")
	private TipoArea tipoArea;

	@Column(name = "NR_NIVEL_VULN")
	private Integer nivelVulnerabilidade;

	@Enumerated(EnumType.STRING)
	@Column(name = "TP_VISIB")
	private TipoVisibilidade tipoVisibilidade;

}
