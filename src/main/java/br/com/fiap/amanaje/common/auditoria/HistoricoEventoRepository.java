package br.com.fiap.amanaje.common.auditoria;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoEventoRepository extends JpaRepository<HistoricoEvento, Long> {
}
