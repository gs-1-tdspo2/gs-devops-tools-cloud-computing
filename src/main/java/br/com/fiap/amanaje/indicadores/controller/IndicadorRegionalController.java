package br.com.fiap.amanaje.indicadores.controller;

import br.com.fiap.amanaje.indicadores.service.IndicadorRegionalService;

import java.util.List;

import br.com.fiap.amanaje.indicadores.dto.IndicadorRegionalResponse;
import br.com.fiap.amanaje.riscos.enums.NivelRisco;
import br.com.fiap.amanaje.riscos.enums.TipoRisco;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/indicadores-regionais")
@Tag(name = "Indicadores Regionais", description = "Consulta de indicadores regionais persistidos")
public class IndicadorRegionalController {

	private final IndicadorRegionalService indicadorService;

	public IndicadorRegionalController(IndicadorRegionalService indicadorService) {
		this.indicadorService = indicadorService;
	}

	@GetMapping
	@Operation(summary = "Listar indicadores regionais persistidos")
	public List<IndicadorRegionalResponse> listar(
			@RequestParam(required = false) String estado,
			@RequestParam(required = false) String cidade,
			@RequestParam(required = false) TipoRisco tipoRisco,
			@RequestParam(required = false) NivelRisco nivelRiscoMedio) {
		return indicadorService.listar(estado, cidade, tipoRisco, nivelRiscoMedio);
	}

}
