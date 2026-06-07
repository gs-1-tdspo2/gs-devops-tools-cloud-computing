package br.com.fiap.amanaje.clientes.dto;

import br.com.fiap.amanaje.clientes.enums.TipoCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClienteCreateRequest(
		@NotBlank @Size(max = 150) String nome,
		@NotNull TipoCliente tipoCliente,
		@Size(max = 30) String documento,
		@Email @Size(max = 200) String emailContato,
		@Size(max = 30) String telefone) {
}
