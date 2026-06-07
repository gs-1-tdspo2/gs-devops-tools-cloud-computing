package br.com.fiap.amanaje.usuarios.model;

import br.com.fiap.amanaje.usuarios.enums.StatusUsuario;

import br.com.fiap.amanaje.usuarios.enums.PerfilUsuario;

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
@Table(name = "TB_AMANAJE_USU")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Usuario extends EntidadeAuditavel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_USUARIO")
	private Long idUsuario;

	@Column(name = "ID_CLIENTE")
	private Long idCliente;

	@Column(name = "NM_USU")
	private String nome;

	@Column(name = "DS_EMAIL")
	private String email;

	@Column(name = "DS_SENHA_HASH")
	private String senhaHash;

	@Enumerated(EnumType.STRING)
	@Column(name = "TP_PERFIL")
	private PerfilUsuario perfil;

	@Enumerated(EnumType.STRING)
	@Column(name = "ST_USU")
	private StatusUsuario statusUsuario;

}
