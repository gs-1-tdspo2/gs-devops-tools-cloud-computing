package br.com.fiap.amanaje.leituras;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.fiap.amanaje.leituras.config.LeituraHttpProperties;
import br.com.fiap.amanaje.leituras.controller.LeituraIotController;
import br.com.fiap.amanaje.leituras.dto.LeituraIotCreateRequest;
import br.com.fiap.amanaje.leituras.dto.LeituraIotResponse;
import br.com.fiap.amanaje.leituras.service.LeituraIotService;
import br.com.fiap.amanaje.riscos.service.RiscoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class LeituraIotControllerTest {

	@Mock
	private LeituraIotService leituraService;

	@Mock
	private RiscoService riscoService;

	@Test
	void shouldEvaluateRiskAfterHttpTelemetryWhenEnabled() {
		LeituraHttpProperties properties = new LeituraHttpProperties();
		properties.setEvaluateRisk(true);
		LeituraIotController controller = new LeituraIotController(leituraService, riscoService, properties);
		LeituraIotCreateRequest request = request();
		LeituraIotResponse saved = response();
		when(leituraService.criar(request)).thenReturn(saved);

		ResponseEntity<LeituraIotResponse> response = controller.criar(request);

		assertThat(response.getBody()).isEqualTo(saved);
		verify(leituraService).criar(request);
		verify(riscoService).avaliar(20L);
	}

	@Test
	void shouldNotEvaluateRiskAfterHttpTelemetryWhenDisabled() {
		LeituraHttpProperties properties = new LeituraHttpProperties();
		properties.setEvaluateRisk(false);
		LeituraIotController controller = new LeituraIotController(leituraService, riscoService, properties);
		LeituraIotCreateRequest request = request();
		LeituraIotResponse saved = response();
		when(leituraService.criar(request)).thenReturn(saved);

		ResponseEntity<LeituraIotResponse> response = controller.criar(request);

		assertThat(response.getBody()).isEqualTo(saved);
		verify(leituraService).criar(request);
		verify(riscoService, never()).avaliar(20L);
	}

	@Test
	void shouldKeepHttpTelemetryResponseWhenRiskEvaluationFails() {
		LeituraHttpProperties properties = new LeituraHttpProperties();
		properties.setEvaluateRisk(true);
		LeituraIotController controller = new LeituraIotController(leituraService, riscoService, properties);
		LeituraIotCreateRequest request = request();
		LeituraIotResponse saved = response();
		when(leituraService.criar(request)).thenReturn(saved);
		when(riscoService.avaliar(20L)).thenThrow(new RuntimeException("risk unavailable"));

		ResponseEntity<LeituraIotResponse> response = controller.criar(request);

		assertThat(response.getBody()).isEqualTo(saved);
		verify(leituraService).criar(request);
		verify(riscoService).avaliar(20L);
	}

	private LeituraIotCreateRequest request() {
		return new LeituraIotCreateRequest(
				null,
				"AMANAJE-SP-RP-001",
				LocalDateTime.parse("2026-05-28T14:30:00"),
				BigDecimal.valueOf(80),
				BigDecimal.valueOf(73),
				BigDecimal.valueOf(18.5),
				BigDecimal.valueOf(0.72),
				BigDecimal.valueOf(998.4),
				BigDecimal.valueOf(118),
				BigDecimal.valueOf(180));
	}

	private LeituraIotResponse response() {
		return new LeituraIotResponse(
				30L,
				10L,
				20L,
				BigDecimal.valueOf(80),
				BigDecimal.valueOf(73),
				BigDecimal.valueOf(18.5),
				BigDecimal.valueOf(0.72),
				BigDecimal.valueOf(998.4),
				BigDecimal.valueOf(118),
				BigDecimal.valueOf(180),
				LocalDateTime.parse("2026-05-28T14:30:00"),
				LocalDateTime.parse("2026-05-28T14:30:01"),
				"S");
	}

}
