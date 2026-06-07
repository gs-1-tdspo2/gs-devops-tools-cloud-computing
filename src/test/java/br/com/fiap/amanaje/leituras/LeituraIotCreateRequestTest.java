package br.com.fiap.amanaje.leituras;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.fiap.amanaje.leituras.dto.LeituraIotCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class LeituraIotCreateRequestTest {

	private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

	@Test
	void shouldParseWokwiEnglishHttpPayloadAliases() throws Exception {
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

		LeituraIotCreateRequest request = objectMapper.readValue(json, LeituraIotCreateRequest.class);

		assertThat(request.codigoEstacao()).isEqualTo("AMANAJE-SP-RP-001");
		assertThat(request.dtLeitura()).isEqualTo(LocalDateTime.of(2026, 5, 28, 14, 30));
		assertThat(request.distanciaAguaCm()).isEqualByComparingTo(BigDecimal.valueOf(80));
		assertThat(request.nivelAguaPercentual()).isEqualByComparingTo(BigDecimal.valueOf(73));
		assertThat(request.inclinacaoGraus()).isEqualByComparingTo("18.5");
		assertThat(request.vibracao()).isEqualByComparingTo("0.72");
		assertThat(request.pressaoHpa()).isEqualByComparingTo("998.4");
		assertThat(request.pm25()).isEqualByComparingTo(BigDecimal.valueOf(118));
		assertThat(request.pm10()).isEqualByComparingTo(BigDecimal.valueOf(180));
	}

	@Test
	void shouldKeepParsingPortugueseHttpPayloadNames() throws Exception {
		String json = """
				{
				  "codigoEstacao": "AMANAJE-SP-RP-001",
				  "dtLeitura": "2026-05-28T14:30:00",
				  "distanciaAguaCm": 80,
				  "nivelAguaPercentual": 73,
				  "inclinacaoGraus": 18.5,
				  "vibracao": 0.72,
				  "pressaoHpa": 998.4,
				  "pm25": 118,
				  "pm10": 180
				}
				""";

		LeituraIotCreateRequest request = objectMapper.readValue(json, LeituraIotCreateRequest.class);

		assertThat(request.codigoEstacao()).isEqualTo("AMANAJE-SP-RP-001");
		assertThat(request.dtLeitura()).isEqualTo(LocalDateTime.of(2026, 5, 28, 14, 30));
		assertThat(request.distanciaAguaCm()).isEqualByComparingTo(BigDecimal.valueOf(80));
		assertThat(request.nivelAguaPercentual()).isEqualByComparingTo(BigDecimal.valueOf(73));
		assertThat(request.inclinacaoGraus()).isEqualByComparingTo("18.5");
		assertThat(request.vibracao()).isEqualByComparingTo("0.72");
		assertThat(request.pressaoHpa()).isEqualByComparingTo("998.4");
		assertThat(request.pm25()).isEqualByComparingTo(BigDecimal.valueOf(118));
		assertThat(request.pm10()).isEqualByComparingTo(BigDecimal.valueOf(180));
	}

}
