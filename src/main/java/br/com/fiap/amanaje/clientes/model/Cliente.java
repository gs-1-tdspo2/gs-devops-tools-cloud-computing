package br.com.fiap.amanaje.clientes.model;

import br.com.fiap.amanaje.clientes.enums.TipoCliente;

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
@Table(name = "TB_AMANAJE_CLI")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cliente extends EntidadeAuditavel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_CLIENTE")
	private Long idCliente;

	@Column(name = "NM_CLI")
	private String nome;

	@Enumerated(EnumType.STRING)
	@Column(name = "TP_CLI")
	private TipoCliente tipoCliente;

	@Column(name = "NR_DOCUMENTO")
	private String documento;

	@Column(name = "DS_EMAIL_CONTATO")
	private String emailContato;

	@Column(name = "NR_TELEFONE")
	private String telefone;

}
