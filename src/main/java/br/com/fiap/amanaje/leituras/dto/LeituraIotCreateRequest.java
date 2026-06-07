package br.com.fiap.amanaje.leituras.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record LeituraIotCreateRequest(
		@Positive Long idEstacao,
		@JsonAlias("stationCode") @Size(max = 40) String codigoEstacao,
		@JsonAlias("timestamp") LocalDateTime dtLeitura,
		@JsonAlias("waterDistanceCm")
		BigDecimal distanciaAguaCm,
		@JsonAlias("waterLevelPercent")
		@DecimalMin("0") @DecimalMax("100") BigDecimal nivelAguaPercentual,
		@JsonAlias("tiltAngle")
		BigDecimal inclinacaoGraus,
		@JsonAlias("vibration")
		BigDecimal vibracao,
		@JsonAlias("pressureHpa")
		@DecimalMin("800") @DecimalMax("1200") BigDecimal pressaoHpa,
		@PositiveOrZero BigDecimal pm25,
		@PositiveOrZero BigDecimal pm10) {
}
