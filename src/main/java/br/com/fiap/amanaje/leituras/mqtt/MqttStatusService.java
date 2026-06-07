package br.com.fiap.amanaje.leituras.mqtt;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.fiap.amanaje.common.model.DadosRedeEstacao;
import br.com.fiap.amanaje.estacoes.model.EstacaoIot;
import br.com.fiap.amanaje.estacoes.model.LogStatusEstacao;
import br.com.fiap.amanaje.estacoes.repository.LogStatusEstacaoRepository;
import br.com.fiap.amanaje.estacoes.service.EstacaoIotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MqttStatusService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MqttStatusService.class);

	private final EstacaoIotService estacaoService;
	private final LogStatusEstacaoRepository logStatusRepository;

	public MqttStatusService(
			EstacaoIotService estacaoService,
			LogStatusEstacaoRepository logStatusRepository) {
		this.estacaoService = estacaoService;
		this.logStatusRepository = logStatusRepository;
	}

	@Transactional
	public void registrar(MqttStatusPayload payload) {
		EstacaoIot estacao = estacaoService.buscarAtivaPorCodigo(payload.stationCode());
		LogStatusEstacao logStatus = LogStatusEstacao.builder()
				.idEstacao(estacao.getIdEstacao())
				.uptimeSegundos(payload.uptimeSeg())
				.rssi(payload.rssi() == null ? null : BigDecimal.valueOf(payload.rssi()))
				.dadosRede(DadosRedeEstacao.builder()
						.ipAddress(payload.ip())
						.versaoFirmware(payload.versaoFirmware())
						.build())
				.dtRegistro(LocalDateTime.now())
				.build();
		logStatusRepository.save(logStatus);
		if (payload.mac() != null) {
			LOGGER.info("Status MQTT recebido com mac={} para stationCode={}; campo não persistido no schema atual.",
					payload.mac(),
					payload.stationCode());
		}
	}

}
