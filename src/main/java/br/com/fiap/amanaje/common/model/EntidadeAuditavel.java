package br.com.fiap.amanaje.common.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class EntidadeAuditavel {

	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(name = "ST_ATIVO", length = 1, columnDefinition = "CHAR(1)")
	private String stAtivo;

	@Column(name = "DT_CRIADO_EM")
	private LocalDateTime dtCriadoEm;

	@Column(name = "DT_ATUALIZADO_EM")
	private LocalDateTime dtAtualizadoEm;

	@Column(name = "DT_DEL_EM")
	private LocalDateTime dtDelEm;

	@Column(name = "ID_DEL_POR")
	private Long idDelPor;

	@Column(name = "DS_MOTIVO_EXCLUSAO")
	private String motivoExclusao;

}
