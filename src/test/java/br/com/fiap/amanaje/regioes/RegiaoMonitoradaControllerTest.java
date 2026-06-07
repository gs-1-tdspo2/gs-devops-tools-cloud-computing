package br.com.fiap.amanaje.regioes;

import br.com.fiap.amanaje.regioes.controller.RegiaoMonitoradaController;
import br.com.fiap.amanaje.regioes.dto.RegiaoResponse;
import br.com.fiap.amanaje.regioes.enums.TipoArea;
import br.com.fiap.amanaje.regioes.enums.TipoVisibilidade;
import br.com.fiap.amanaje.regioes.service.RegiaoMonitoradaService;
import br.com.fiap.amanaje.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegiaoMonitoradaController.class)
class RegiaoMonitoradaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RegiaoMonitoradaService regiaoService;

	@Test
	void shouldReturnRegiaoById() throws Exception {
		when(regiaoService.buscarPorId(3L)).thenReturn(new RegiaoResponse(
				3L,
				2L,
				"Comunidade Ribeirinha Educandos",
				"Manaus",
				"AM",
				new BigDecimal("-3.1333"),
				new BigDecimal("-60.0151"),
				TipoArea.REGIAO_RIBEIRINHA,
				76,
				TipoVisibilidade.INSTITUCIONAL,
				"S",
				LocalDateTime.parse("2026-06-02T12:07:50.391830"),
				null));

		mockMvc.perform(get("/api/regioes/3"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.idRegiao").value(3))
				.andExpect(jsonPath("$.idCliente").value(2))
				.andExpect(jsonPath("$.nome").value("Comunidade Ribeirinha Educandos"))
				.andExpect(jsonPath("$.cidade").value("Manaus"))
				.andExpect(jsonPath("$.estado").value("AM"))
				.andExpect(jsonPath("$.tipoArea").value("REGIAO_RIBEIRINHA"))
				.andExpect(jsonPath("$.nivelVulnerabilidade").value(76))
				.andExpect(jsonPath("$.tipoVisibilidade").value("INSTITUCIONAL"))
				.andExpect(jsonPath("$.stAtivo").value("S"));
	}

	@Test
	void shouldReturnNotFoundWhenRegiaoDoesNotExist() throws Exception {
		when(regiaoService.buscarPorId(999999L))
				.thenThrow(new ResourceNotFoundException("Região monitorada ativa não encontrada: 999999"));

		mockMvc.perform(get("/api/regioes/999999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message").value("Região monitorada ativa não encontrada: 999999"));
	}

}
