package br.com.fiap.amanaje.leituras.mqtt;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class MqttLeituraPayloadTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void shouldParseSampleTelemetryJson() throws Exception {
		String json = """
				{
				  "stationCode": "APP-ST-001",
				  "timestamp": "2026-06-03T18:36:35",
				  "waterDistanceCm": 399.94,
				  "waterLevelPercent": 0,
				  "tiltAngle": 0.00,
				  "vibration": 0.00,
				  "pressureHpa": 1013.27,
				  "pm25": 0.00,
				  "pm10": 0.00
				}
				""";

		objectMapper.findAndRegisterModules();
		MqttLeituraPayload payload = objectMapper.readValue(json, MqttLeituraPayload.class);

		assertThat(payload.codigoEstacao()).isEqualTo("APP-ST-001");
		assertThat(payload.dtLeitura()).isEqualTo(LocalDateTime.of(2026, 6, 3, 18, 36, 35));
		assertThat(payload.distanciaAguaCm()).isEqualByComparingTo("399.94");
		assertThat(payload.nivelAguaPercentual()).isEqualByComparingTo(BigDecimal.ZERO);
		assertThat(payload.inclinacaoGraus()).isEqualByComparingTo("0.00");
		assertThat(payload.vibracao()).isEqualByComparingTo("0.00");
		assertThat(payload.pressaoHpa()).isEqualByComparingTo("1013.27");
		assertThat(payload.pm25()).isEqualByComparingTo("0.00");
		assertThat(payload.pm10()).isEqualByComparingTo("0.00");
	}

}
