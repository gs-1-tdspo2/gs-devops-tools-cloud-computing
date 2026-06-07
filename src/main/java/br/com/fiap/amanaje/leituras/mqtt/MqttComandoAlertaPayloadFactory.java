package br.com.fiap.amanaje.leituras.mqtt;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;

import br.com.fiap.amanaje.riscos.dto.AvaliacaoRiscoResponse;
import br.com.fiap.amanaje.riscos.dto.AvaliarRiscoResponse;
import br.com.fiap.amanaje.riscos.enums.NivelRisco;
import br.com.fiap.amanaje.riscos.enums.TipoRisco;
import org.springframework.stereotype.Component;

@Component
public class MqttComandoAlertaPayloadFactory {

	public MqttComandoAlertaPayload fromRisk(String stationCode, AvaliarRiscoResponse risco) {
		AvaliacaoRiscoResponse principal = risco.avaliacoes().stream()
				.max(Comparator
						.comparing(AvaliacaoRiscoResponse::scoreRisco)
						.thenComparing(avaliacao -> avaliacao.tipoRisco().name()))
				.orElseThrow();
		return build(
				stationCode,
				principal.nivelRisco(),
				principal.tipoRisco(),
				principal.scoreRisco());
	}

	public MqttComandoAlertaPayload monitoringOnly(String stationCode) {
		return build(stationCode, NivelRisco.BAIXO, null, BigDecimal.ZERO);
	}

	private MqttComandoAlertaPayload build(
			String stationCode,
			NivelRisco nivelRisco,
			TipoRisco tipoRiscoPrincipal,
			BigDecimal score) {
		return new MqttComandoAlertaPayload(
				stationCode,
				nivelRisco,
				tipoRiscoPrincipal,
				score,
				MqttOutputMapper.isAlerta(nivelRisco),
				MqttOutputMapper.ledVerde(nivelRisco),
				MqttOutputMapper.ledVermelho(nivelRisco),
				MqttOutputMapper.buzzer(nivelRisco),
				MqttOutputMapper.mensagem(nivelRisco),
				LocalDateTime.now());
	}

}
