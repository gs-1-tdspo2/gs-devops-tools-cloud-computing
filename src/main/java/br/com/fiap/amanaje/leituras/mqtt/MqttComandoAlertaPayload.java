package br.com.fiap.amanaje.leituras.mqtt;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.fiap.amanaje.riscos.enums.NivelRisco;
import br.com.fiap.amanaje.riscos.enums.TipoRisco;

public record MqttComandoAlertaPayload(
		String stationCode,
		NivelRisco nivelRisco,
		TipoRisco tipoRiscoPrincipal,
		BigDecimal score,
		boolean alerta,
		boolean ledVerde,
		boolean ledVermelho,
		boolean buzzer,
		String mensagem,
		LocalDateTime timestamp) {
}
