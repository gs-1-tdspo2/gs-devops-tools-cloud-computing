package br.com.fiap.amanaje.usuarios.dto;

import java.time.LocalDateTime;

import br.com.fiap.amanaje.usuarios.enums.PerfilUsuario;
import br.com.fiap.amanaje.usuarios.enums.StatusUsuario;

public record UsuarioResponse(
		Long idUsuario,
		Long idCliente,
		String nome,
		String email,
		PerfilUsuario perfil,
		StatusUsuario status,
		String stAtivo,
		LocalDateTime criadoEm,
		LocalDateTime atualizadoEm) {
}
