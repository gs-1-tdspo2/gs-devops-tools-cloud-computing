package br.com.fiap.amanaje.leituras.model;

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
@Table(name = "TB_AMANAJE_LEIT_IOT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeituraIot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_LEITURA")
	private Long idLeitura;

	@Column(name = "ID_ESTACAO")
	private Long idEstacao;

	@Column(name = "ID_REGIAO")
	private Long idRegiao;

	@Column(name = "NR_DISTANCIA_AGUA_CM")
	private BigDecimal distanciaAguaCm;

	@Column(name = "NR_NIVEL_AGUA_PCT")
	private BigDecimal nivelAguaPercentual;

	@Column(name = "NR_INCL_GRAUS")
	private BigDecimal inclinacaoGraus;

	@Column(name = "NR_VIBRACAO")
	private BigDecimal vibracao;

	@Column(name = "NR_PRESSAO_HPA")
	private BigDecimal pressaoHpa;

	@Column(name = "NR_PM25")
	private BigDecimal pm25;

	@Column(name = "NR_PM10")
	private BigDecimal pm10;

	@Column(name = "DT_LEIT")
	private LocalDateTime dtLeitura;

	@Column(name = "DT_RECEBIDO_EM")
	private LocalDateTime dtRecebidoEm;

	@Column(name = "ST_VALIDA", length = 1, columnDefinition = "CHAR(1)")
	private String stValida;

	@Column(name = "DS_MOTIVO_INVAL")
	private String motivoInvalidacao;

}
