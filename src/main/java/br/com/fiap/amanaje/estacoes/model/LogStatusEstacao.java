package br.com.fiap.amanaje.estacoes.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.fiap.amanaje.common.model.DadosRedeEstacao;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
@Table(name = "TB_AMANAJE_LOG_STATUS_EST")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogStatusEstacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_LOG")
	private Long idLog;

	@Column(name = "ID_ESTACAO")
	private Long idEstacao;

	@Column(name = "NR_UPTIME_SEG")
	private Long uptimeSegundos;

	@Column(name = "NR_RSSI")
	private BigDecimal rssi;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "ipAddress", column = @Column(name = "DS_IP_ADDRESS")),
			@AttributeOverride(name = "versaoFirmware", column = @Column(name = "DS_VERSAO_FIRMWARE"))
	})
	private DadosRedeEstacao dadosRede;

	@Column(name = "DT_REGISTRO")
	private LocalDateTime dtRegistro;

}
