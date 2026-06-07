package br.com.fiap.amanaje.dashboard.controller;

import br.com.fiap.amanaje.dashboard.service.DashboardService;

import br.com.fiap.amanaje.dashboard.dto.DashboardSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Resumo operacional do monitoramento ambiental")
public class DashboardController {

	private final DashboardService dashboardService;

	public DashboardController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	@GetMapping("/summary")
	@Operation(summary = "Consultar resumo operacional do dashboard")
	public DashboardSummaryResponse buscarResumo(@RequestParam(required = false) Long idCliente) {
		return dashboardService.buscarResumo(idCliente);
	}

}
