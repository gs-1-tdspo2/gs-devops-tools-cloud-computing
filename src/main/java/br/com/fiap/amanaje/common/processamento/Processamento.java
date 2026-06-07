package br.com.fiap.amanaje.common.processamento;

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
@Table(name = "TB_AMANAJE_PROCESS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Processamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_PROCESSAMENTO")
	private Long idProcessamento;

	@Column(name = "ID_REGIAO")
	private Long idRegiao;

	@Column(name = "ID_USUARIO")
	private Long idUsuario;

	@Enumerated(EnumType.STRING)
	@Column(name = "TP_PROCESS")
	private TipoProcessamento tipoProcessamento;

	@Enumerated(EnumType.STRING)
	@Column(name = "ST_PROCESS")
	private StatusProcessamento statusProcessamento;

	@Column(name = "DS_ORIGEM")
	private String origem;

	@Column(name = "DS_PARAM")
	private String parametros;

	@Column(name = "DS_RESULT")
	private String resultado;

	@Column(name = "DT_INICIO")
	private LocalDateTime dtInicio;

	@Column(name = "DT_FIM")
	private LocalDateTime dtFim;

}
