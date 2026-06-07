package br.com.fiap.amanaje.leituras.mqtt;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class MqttComandoAlertaPublisherTest {

	@Test
	void shouldGenerateCommandTopicFromStationCode() {
		MqttProperties properties = new MqttProperties();
		properties.setCommandTopicPattern("app/estacoes/%s/alertas");
		MqttComandoAlertaPublisher publisher = new MqttComandoAlertaPublisher(properties, new ObjectMapper());

		assertThat(publisher.topicFor("APP-ST-001")).isEqualTo("app/estacoes/APP-ST-001/alertas");
	}

}
