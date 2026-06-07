package br.com.fiap.amanaje.usuarios.repository;

import br.com.fiap.amanaje.usuarios.model.Usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByEmail(String email);

	List<Usuario> findByStAtivo(String stAtivo);

	List<Usuario> findByIdClienteAndStAtivo(Long idCliente, String stAtivo);

}
