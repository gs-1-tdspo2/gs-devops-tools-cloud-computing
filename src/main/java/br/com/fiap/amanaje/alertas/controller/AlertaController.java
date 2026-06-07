package br.com.fiap.amanaje.alertas.controller;

import br.com.fiap.amanaje.alertas.enums.StatusAlerta;

import br.com.fiap.amanaje.alertas.service.AlertaService;

import br.com.fiap.amanaje.alertas.model.Alerta;

import java.util.List;

import br.com.fiap.amanaje.alertas.dto.AlertaResponse;
import br.com.fiap.amanaje.riscos.enums.NivelRisco;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alertas")
@Tag(name = "Alertas", description = "Consulta e resolução de alertas ambientais")
public class AlertaController {

	private final AlertaService alertaService;

	public AlertaController(AlertaService alertaService) {
		this.alertaService = alertaService;
	}

	@GetMapping
	@Operation(summary = "Listar alertas ativos")
	public List<AlertaResponse> listar(
			@RequestParam(required = false) Long idRegiao,
			@RequestParam(name = "status", required = false) StatusAlerta statusAlerta,
			@RequestParam(name = "nivel", required = false) NivelRisco nivelRisco) {
		return alertaService.listarAtivos(idRegiao, statusAlerta, nivelRisco);
	}

	@PutMapping("/{id}/resolver")
	@Operation(summary = "Resolver alerta ativo")
	public AlertaResponse resolver(@PathVariable Long id) {
		return alertaService.resolver(id);
	}

}
