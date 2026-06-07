package br.com.fiap.amanaje.indicadores.repository;

import br.com.fiap.amanaje.indicadores.model.IndicadorRegional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IndicadorRegionalRepository extends JpaRepository<IndicadorRegional, Long> {

	List<IndicadorRegional> findByEstadoAndCidade(String estado, String cidade);

}
