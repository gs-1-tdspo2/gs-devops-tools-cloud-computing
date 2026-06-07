package br.com.fiap.amanaje.clientes.repository;

import br.com.fiap.amanaje.clientes.model.Cliente;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

	List<Cliente> findByStAtivo(String stAtivo);

}
