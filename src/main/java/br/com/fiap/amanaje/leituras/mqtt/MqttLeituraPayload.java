package br.com.fiap.amanaje.leituras.mqtt;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;

public record MqttLeituraPayload(
		Long idEstacao,
		@JsonAlias("stationCode")
		String codigoEstacao,
		@JsonAlias("timestamp")
		LocalDateTime dtLeitura,
		@JsonAlias("waterDistanceCm")
		BigDecimal distanciaAguaCm,
		@JsonAlias("waterLevelPercent")
		BigDecimal nivelAguaPercentual,
		@JsonAlias("tiltAngle")
		BigDecimal inclinacaoGraus,
		@JsonAlias("vibration")
		BigDecimal vibracao,
		@JsonAlias("pressureHpa")
		BigDecimal pressaoHpa,
		BigDecimal pm25,
		BigDecimal pm10) {
}
