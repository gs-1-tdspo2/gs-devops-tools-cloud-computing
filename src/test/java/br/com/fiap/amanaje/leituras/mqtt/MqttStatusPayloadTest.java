package br.com.fiap.amanaje.leituras.mqtt;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class MqttStatusPayloadTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void shouldParseSampleStatusJson() throws Exception {
		String json = """
				{
				  "stationCode": "APP-ST-001",
				  "mac": "24:0A:C4:00:01:10",
				  "uptimeSeg": 24,
				  "rssi": -94,
				  "ip": "10.13.37.2",
				  "versaoFirmware": "1.4.0"
				}
				""";

		MqttStatusPayload payload = objectMapper.readValue(json, MqttStatusPayload.class);

		assertThat(payload.stationCode()).isEqualTo("APP-ST-001");
		assertThat(payload.mac()).isEqualTo("24:0A:C4:00:01:10");
		assertThat(payload.uptimeSeg()).isEqualTo(24);
		assertThat(payload.rssi()).isEqualTo(-94);
		assertThat(payload.ip()).isEqualTo("10.13.37.2");
		assertThat(payload.versaoFirmware()).isEqualTo("1.4.0");
	}

}
