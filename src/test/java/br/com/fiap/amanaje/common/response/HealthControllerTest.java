package br.com.fiap.amanaje.common.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HealthControllerTest {

	@Test
	void shouldReturnApplicationHealthInformation() {
		HealthController.HealthResponse response = new HealthController().health();

		assertThat(response.application()).isEqualTo("Amanajé API");
		assertThat(response.status()).isEqualTo("UP");
		assertThat(response.message()).isEqualTo("API principal do Amanajé em execução");
		assertThat(response.timestamp()).isNotNull();
	}

}
