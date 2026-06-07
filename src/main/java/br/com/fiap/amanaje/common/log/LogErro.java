package br.com.fiap.amanaje.common.log;

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
@Table(name = "TB_AMANAJE_LOG_ERRO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogErro {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_LOG_ERRO")
	private Long idLogErro;

	@Column(name = "ID_PROCESSAMENTO")
	private Long idProcessamento;

	@Column(name = "ID_USUARIO")
	private Long idUsuario;

	@Column(name = "NM_ORIGEM")
	private String origem;

	@Column(name = "NM_OBJETO")
	private String objeto;

	@Column(name = "CD_ERRO")
	private Long codigoErro;

	@Column(name = "DS_ERRO")
	private String descricao;

	@Column(name = "DS_COMANDO")
	private String comando;

	@Column(name = "DT_ERRO")
	private LocalDateTime dtErro;

}
