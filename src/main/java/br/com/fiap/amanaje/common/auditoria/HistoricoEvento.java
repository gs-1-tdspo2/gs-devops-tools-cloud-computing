package br.com.fiap.amanaje.common.auditoria;

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
@Table(name = "TB_AMANAJE_HIST_EVENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricoEvento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_EVENTO")
	private Long idEvento;

	@Column(name = "ID_USUARIO")
	private Long idUsuario;

	@Column(name = "NM_ENTIDADE")
	private String entidade;

	@Column(name = "ID_ENTIDADE")
	private Long idEntidade;

	@Enumerated(EnumType.STRING)
	@Column(name = "TP_ACAO")
	private TipoAcaoHistorico tipoAcao;

	@Column(name = "DS_EVENTO")
	private String descricao;

	@Column(name = "DT_EVENTO")
	private LocalDateTime dtEvento;

}
