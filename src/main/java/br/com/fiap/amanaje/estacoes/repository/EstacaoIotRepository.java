package br.com.fiap.amanaje.estacoes.repository;

import br.com.fiap.amanaje.estacoes.model.EstacaoIot;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EstacaoIotRepository extends JpaRepository<EstacaoIot, Long> {

	List<EstacaoIot> findByIdRegiaoAndStAtivo(Long idRegiao, String stAtivo);

	List<EstacaoIot> findByStAtivo(String stAtivo);

	Optional<EstacaoIot> findByCodigoEstacao(String codigoEstacao);

}
