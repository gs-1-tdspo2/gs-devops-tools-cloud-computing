package br.com.fiap.amanaje.leituras.mqtt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import br.com.fiap.amanaje.leituras.dto.LeituraIotResponse;
import br.com.fiap.amanaje.leituras.service.LeituraIotService;
import br.com.fiap.amanaje.riscos.dto.AvaliarRiscoResponse;
import br.com.fiap.amanaje.riscos.enums.NivelRisco;
import br.com.fiap.amanaje.riscos.service.RiscoService;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

@ExtendWith(MockitoExtension.class)
class MqttLeituraSubscriberTest {

	@Mock
	private LeituraIotService leituraService;

	@Mock
	private RiscoService riscoService;

	@Mock
	private MqttStatusService statusService;

	@Mock
	private MqttComandoAlertaPublisher comandoAlertaPublisher;

	@Mock
	private MqttComandoAlertaPayloadFactory comandoAlertaPayloadFactory;

	@Test
	void shouldNotConnectOrProcessWhenDisabled() {
		MqttProperties properties = new MqttProperties();
		properties.setEnabled(false);
		MqttLeituraSubscriber subscriber = new MqttLeituraSubscriber(
				properties,
				new ObjectMapper(),
				leituraService,
				riscoService,
				statusService,
				comandoAlertaPublisher,
				comandoAlertaPayloadFactory);

		subscriber.run(new DefaultApplicationArguments());

		verify(leituraService, never()).criar(org.mockito.ArgumentMatchers.any());
		verify(riscoService, never()).avaliar(org.mockito.ArgumentMatchers.any());
		verify(statusService, never()).registrar(org.mockito.ArgumentMatchers.any());
		verify(comandoAlertaPublisher, never()).publish(org.mockito.ArgumentMatchers.any());
	}

	@Test
	void shouldEvaluateRiskOnceWhenMqttTelemetryEvaluationIsEnabled() {
		MqttProperties properties = new MqttProperties();
		properties.setEvaluateRiskOnMessage(true);
		ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
		MqttLeituraSubscriber subscriber = new MqttLeituraSubscriber(
				properties,
				objectMapper,
				leituraService,
				riscoService,
				statusService,
				comandoAlertaPublisher,
				comandoAlertaPayloadFactory);
		LeituraIotResponse leitura = new LeituraIotResponse(
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
		AvaliarRiscoResponse risco = new AvaliarRiscoResponse(20L, List.of(), List.of());
		MqttComandoAlertaPayload comando = new MqttComandoAlertaPayload(
				"AMANAJE-SP-RP-001",
				NivelRisco.BAIXO,
				null,
				BigDecimal.ZERO,
				false,
				true,
				false,
				false,
				"Condição estável. Sistema OK.",
				LocalDateTime.now());
		when(leituraService.criar(any())).thenReturn(leitura);
		when(riscoService.avaliar(20L)).thenReturn(risco);
		when(comandoAlertaPayloadFactory.fromRisk(eq("AMANAJE-SP-RP-001"), eq(risco))).thenReturn(comando);

		String json = """
				{
				  "stationCode": "AMANAJE-SP-RP-001",
				  "timestamp": "2026-05-28T14:30:00",
				  "waterDistanceCm": 80,
				  "waterLevelPercent": 73,
				  "tiltAngle": 18.5,
				  "vibration": 0.72,
				  "pressureHpa": 998.4,
				  "pm25": 118,
				  "pm10": 180
				}
				""";
		subscriber.handleTelemetryMessage(
				"app/estacoes/AMANAJE-SP-RP-001/telemetria",
				new MqttMessage(json.getBytes()));

		verify(leituraService).criar(any());
		verify(riscoService).avaliar(20L);
		verify(comandoAlertaPayloadFactory).fromRisk("AMANAJE-SP-RP-001", risco);
		verify(comandoAlertaPublisher).publish(comando);
	}

}
