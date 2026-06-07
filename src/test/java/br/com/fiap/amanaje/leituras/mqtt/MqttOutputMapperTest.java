package br.com.fiap.amanaje.leituras.mqtt;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.fiap.amanaje.riscos.enums.NivelRisco;
import org.junit.jupiter.api.Test;

class MqttOutputMapperTest {

	@Test
	void shouldMapOutputsForEachRiskLevel() {
		assertOutputs(NivelRisco.BAIXO, false, true, false, false);
		assertOutputs(NivelRisco.MODERADO, false, true, false, false);
		assertOutputs(NivelRisco.ALTO, true, false, true, false);
		assertOutputs(NivelRisco.CRITICO, true, false, true, true);
	}

	private void assertOutputs(
			NivelRisco nivelRisco,
			boolean alerta,
			boolean ledVerde,
			boolean ledVermelho,
			boolean buzzer) {
		assertThat(MqttOutputMapper.isAlerta(nivelRisco)).isEqualTo(alerta);
		assertThat(MqttOutputMapper.ledVerde(nivelRisco)).isEqualTo(ledVerde);
		assertThat(MqttOutputMapper.ledVermelho(nivelRisco)).isEqualTo(ledVermelho);
		assertThat(MqttOutputMapper.buzzer(nivelRisco)).isEqualTo(buzzer);
	}

}
