package br.com.fiap.amanaje.leituras.mqtt;

import com.fasterxml.jackson.annotation.JsonAlias;

public record MqttStatusPayload(
		@JsonAlias("codigoEstacao")
		String stationCode,
		String mac,
		Long uptimeSeg,
		Integer rssi,
		String ip,
		String versaoFirmware) {
}
