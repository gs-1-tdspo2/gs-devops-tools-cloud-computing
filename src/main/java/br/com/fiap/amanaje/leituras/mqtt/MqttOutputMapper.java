package br.com.fiap.amanaje.leituras.mqtt;

import br.com.fiap.amanaje.riscos.enums.NivelRisco;

public final class MqttOutputMapper {

	private MqttOutputMapper() {
	}

	public static boolean isAlerta(NivelRisco nivelRisco) {
		return nivelRisco == NivelRisco.ALTO || nivelRisco == NivelRisco.CRITICO;
	}

	public static boolean ledVerde(NivelRisco nivelRisco) {
		return nivelRisco == NivelRisco.BAIXO || nivelRisco == NivelRisco.MODERADO;
	}

	public static boolean ledVermelho(NivelRisco nivelRisco) {
		return nivelRisco == NivelRisco.ALTO || nivelRisco == NivelRisco.CRITICO;
	}

	public static boolean buzzer(NivelRisco nivelRisco) {
		return nivelRisco == NivelRisco.CRITICO;
	}

	public static String mensagem(NivelRisco nivelRisco) {
		return switch (nivelRisco) {
			case BAIXO -> "Condição estável. Sistema OK.";
			case MODERADO -> "Atenção: risco moderado detectado. Manter monitoramento.";
			case ALTO -> "Risco alto detectado. Acionar protocolo preventivo.";
			case CRITICO -> "Risco crítico detectado. Acionar alerta preventivo imediatamente.";
		};
	}

}
