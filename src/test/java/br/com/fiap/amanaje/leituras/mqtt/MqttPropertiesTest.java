package br.com.fiap.amanaje.leituras.mqtt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MqttPropertiesTest {

	@Test
	void shouldExposeDefaultValues() {
		MqttProperties properties = new MqttProperties();

		assertThat(properties.isEnabled()).isFalse();
		assertThat(properties.getBrokerUrl()).isEqualTo("tcp://mqtt-dashboard.com:1883");
		assertThat(properties.getClientId()).isEqualTo("amanaje-java-api");
		assertThat(properties.getUsername()).isEmpty();
		assertThat(properties.getPassword()).isEmpty();
		assertThat(properties.getTelemetryTopic()).isEqualTo("app/estacoes/+/telemetria");
		assertThat(properties.getStatusTopic()).isEqualTo("app/estacoes/+/status");
		assertThat(properties.getCommandTopicPattern()).isEqualTo("app/estacoes/%s/alertas");
		assertThat(properties.isEvaluateRiskOnMessage()).isTrue();
		assertThat(properties.getConnectionTimeoutSeconds()).isEqualTo(5);
		assertThat(properties.getKeepAliveIntervalSeconds()).isEqualTo(30);
	}

}
