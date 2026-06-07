package br.com.fiap.amanaje.clientes.dto;

import java.time.LocalDateTime;

import br.com.fiap.amanaje.clientes.enums.TipoCliente;

public record ClienteResponse(
		Long idCliente,
		String nome,
		TipoCliente tipoCliente,
		String documento,
		String emailContato,
		String telefone,
		String stAtivo,
		LocalDateTime dtCriadoEm,
		LocalDateTime dtAtualizadoEm) {
}
