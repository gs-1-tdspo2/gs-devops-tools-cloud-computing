package br.com.fiap.amanaje.observacoes.repository;

import br.com.fiap.amanaje.observacoes.model.ObservacaoClimatica;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ObservacaoClimaticaRepository extends JpaRepository<ObservacaoClimatica, Long> {

	Optional<ObservacaoClimatica> findFirstByIdRegiaoOrderByDtObservacaoDescDtCriadoEmDesc(Long idRegiao);

}
