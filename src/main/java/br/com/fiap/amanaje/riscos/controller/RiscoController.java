package br.com.fiap.amanaje.riscos.controller;

import br.com.fiap.amanaje.riscos.service.RiscoService;

import br.com.fiap.amanaje.riscos.dto.AvaliarRiscoResponse;
import br.com.fiap.amanaje.riscos.dto.RiscoAtualResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Riscos", description = "Avaliação determinística de riscos ambientais por região")
public class RiscoController {

	private final RiscoService riscoService;

	public RiscoController(RiscoService riscoService) {
		this.riscoService = riscoService;
	}

	@PostMapping("/riscos/avaliar/{idRegiao}")
	@Operation(summary = "Avaliar riscos ambientais de uma região")
	public AvaliarRiscoResponse avaliar(@PathVariable Long idRegiao) {
		return riscoService.avaliar(idRegiao);
	}

	@GetMapping("/regioes/{id}/risco-atual")
	@Operation(summary = "Buscar risco atual consolidado de uma região")
	public RiscoAtualResponse buscarAtual(@PathVariable Long id) {
		return riscoService.buscarAtual(id);
	}

}
