package br.com.fiap.amanaje.usuarios.dto;

import br.com.fiap.amanaje.usuarios.enums.PerfilUsuario;
import br.com.fiap.amanaje.usuarios.enums.StatusUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequest(
		@NotBlank @Size(max = 120) String nome,
		@NotBlank @Email @Size(max = 200) String email,
		@NotBlank @Size(max = 255) String senhaHash,
		@NotNull PerfilUsuario perfil,
		@NotNull StatusUsuario status) {
}
