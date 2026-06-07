package br.com.fiap.amanaje.leituras.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LeituraIotResponse(
		Long idLeitura,
		Long idEstacao,
		Long idRegiao,
		BigDecimal distanciaAguaCm,
		BigDecimal nivelAguaPercentual,
		BigDecimal inclinacaoGraus,
		BigDecimal vibracao,
		BigDecimal pressaoHpa,
		BigDecimal pm25,
		BigDecimal pm10,
		LocalDateTime dtLeitura,
		LocalDateTime dtRecebidoEm,
		String stValida) {
}
