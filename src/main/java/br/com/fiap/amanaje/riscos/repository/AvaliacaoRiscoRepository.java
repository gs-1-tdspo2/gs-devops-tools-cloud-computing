package br.com.fiap.amanaje.riscos.repository;

import br.com.fiap.amanaje.riscos.enums.TipoRisco;

import br.com.fiap.amanaje.riscos.model.AvaliacaoRisco;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoRiscoRepository extends JpaRepository<AvaliacaoRisco, Long> {

	List<AvaliacaoRisco> findByIdRegiaoOrderByDtAvaliacaoDesc(Long idRegiao);

	Optional<AvaliacaoRisco> findFirstByIdRegiaoOrderByDtAvaliacaoDesc(Long idRegiao);

	Optional<AvaliacaoRisco> findFirstByIdRegiaoAndTipoRiscoOrderByDtAvaliacaoDesc(
			Long idRegiao,
			TipoRisco tipoRisco);

}
