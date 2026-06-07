package br.com.fiap.amanaje.leituras.repository;

import br.com.fiap.amanaje.leituras.model.LeituraIot;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LeituraIotRepository extends JpaRepository<LeituraIot, Long> {

	List<LeituraIot> findByIdRegiaoOrderByDtLeituraDesc(Long idRegiao);

	List<LeituraIot> findByStValida(String stValida);

	Optional<LeituraIot> findFirstByIdRegiaoAndStValidaOrderByDtLeituraDesc(Long idRegiao, String stValida);

}
