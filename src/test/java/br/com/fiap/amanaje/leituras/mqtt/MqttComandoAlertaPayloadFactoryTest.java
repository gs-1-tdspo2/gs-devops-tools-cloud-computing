package br.com.fiap.amanaje.leituras.mqtt;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.amanaje.riscos.dto.AvaliacaoRiscoResponse;
import br.com.fiap.amanaje.riscos.dto.AvaliarRiscoResponse;
import br.com.fiap.amanaje.riscos.enums.NivelRisco;
import br.com.fiap.amanaje.riscos.enums.TipoRisco;
import org.junit.jupiter.api.Test;

class MqttComandoAlertaPayloadFactoryTest {

	private final MqttComandoAlertaPayloadFactory factory = new MqttComandoAlertaPayloadFactory();

	@Test
	void shouldCreateCommandFromHighestRiskScore() {
		AvaliarRiscoResponse risco = new AvaliarRiscoResponse(
				1L,
				List.of(
						avaliacao(TipoRisco.ENCHENTE, NivelRisco.ALTO, "62"),
						avaliacao(TipoRisco.DESLIZAMENTO, NivelRisco.MODERADO, "35"),
						avaliacao(TipoRisco.QUALIDADE_AR, NivelRisco.CRITICO, "88")),
				List.of());

		MqttComandoAlertaPayload payload = factory.fromRisk("APP-ST-001", risco);

		assertThat(payload.stationCode()).isEqualTo("APP-ST-001");
		assertThat(payload.tipoRiscoPrincipal()).isEqualTo(TipoRisco.QUALIDADE_AR);
		assertThat(payload.nivelRisco()).isEqualTo(NivelRisco.CRITICO);
		assertThat(payload.score()).isEqualByComparingTo("88");
		assertThat(payload.alerta()).isTrue();
		assertThat(payload.ledVerde()).isFalse();
		assertThat(payload.ledVermelho()).isTrue();
		assertThat(payload.buzzer()).isTrue();
		assertThat(payload.timestamp()).isNotNull();
	}

	private AvaliacaoRiscoResponse avaliacao(TipoRisco tipoRisco, NivelRisco nivelRisco, String score) {
		return new AvaliacaoRiscoResponse(
				1L,
				1L,
				10L,
				null,
				tipoRisco,
				new BigDecimal(score),
				nivelRisco,
				"motivo",
				LocalDateTime.now());
	}

}
