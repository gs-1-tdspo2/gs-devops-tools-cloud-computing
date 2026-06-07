package br.com.fiap.amanaje.leituras.mqtt;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.fiap.amanaje.leituras.dto.LeituraIotCreateRequest;
import br.com.fiap.amanaje.leituras.dto.LeituraIotResponse;
import br.com.fiap.amanaje.leituras.service.LeituraIotService;
import br.com.fiap.amanaje.riscos.dto.AvaliarRiscoResponse;
import br.com.fiap.amanaje.riscos.service.RiscoService;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MqttLeituraSubscriber implements ApplicationRunner, DisposableBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(MqttLeituraSubscriber.class);
	private static final int QOS = 1;

	private final MqttProperties properties;
	private final ObjectMapper objectMapper;
	private final LeituraIotService leituraService;
	private final RiscoService riscoService;
	private final MqttStatusService statusService;
	private final MqttComandoAlertaPublisher comandoAlertaPublisher;
	private final MqttComandoAlertaPayloadFactory comandoAlertaPayloadFactory;
	private final ExecutorService mqttExecutor;
	private MqttClient client;

	public MqttLeituraSubscriber(
			MqttProperties properties,
			ObjectMapper objectMapper,
			LeituraIotService leituraService,
			RiscoService riscoService,
			MqttStatusService statusService,
			MqttComandoAlertaPublisher comandoAlertaPublisher,
			MqttComandoAlertaPayloadFactory comandoAlertaPayloadFactory) {
		this.properties = properties;
		this.objectMapper = objectMapper;
		this.leituraService = leituraService;
		this.riscoService = riscoService;
		this.statusService = statusService;
		this.comandoAlertaPublisher = comandoAlertaPublisher;
		this.comandoAlertaPayloadFactory = comandoAlertaPayloadFactory;
		this.mqttExecutor = Executors.newSingleThreadExecutor(runnable -> {
			Thread thread = new Thread(runnable, "amanaje-mqtt-subscriber");
			thread.setDaemon(true);
			return thread;
		});
	}

	@Override
	public void run(ApplicationArguments args) {
		if (!properties.isEnabled()) {
			LOGGER.info("MQTT desabilitado. Nenhuma conexão com broker será iniciada.");
			return;
		}
		LOGGER.info("Inicialização MQTT agendada em background broker={} telemetryTopic={} statusTopic={} clientId={} evaluateRisk={}",
				properties.getBrokerUrl(),
				properties.getTelemetryTopic(),
				properties.getStatusTopic(),
				properties.getClientId(),
				properties.isEvaluateRiskOnMessage());
		mqttExecutor.submit(this::connectAndSubscribe);
	}

	private void connectAndSubscribe() {
		try {
			client = new MqttClient(properties.getBrokerUrl(), properties.getClientId(), new MemoryPersistence());
			client.setCallback(callback());
			client.connect(connectOptions());
			client.subscribe(properties.getTelemetryTopic(), QOS, leituraListener());
			client.subscribe(properties.getStatusTopic(), QOS, statusListener());
			LOGGER.info("MQTT conectado ao broker={} e inscrito em telemetryTopic={} statusTopic={}",
					properties.getBrokerUrl(),
					properties.getTelemetryTopic(),
					properties.getStatusTopic());
		}
		catch (MqttException ex) {
			LOGGER.error("Falha ao conectar/subscrever MQTT no broker={} telemetryTopic={} statusTopic={}: {}",
					properties.getBrokerUrl(),
					properties.getTelemetryTopic(),
					properties.getStatusTopic(),
					ex.getMessage(),
					ex);
		}
	}

	private MqttConnectOptions connectOptions() {
		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(true);
		options.setAutomaticReconnect(true);
		options.setConnectionTimeout(properties.getConnectionTimeoutSeconds());
		options.setKeepAliveInterval(properties.getKeepAliveIntervalSeconds());
		if (StringUtils.hasText(properties.getUsername())) {
			options.setUserName(properties.getUsername());
		}
		if (StringUtils.hasText(properties.getPassword())) {
			options.setPassword(properties.getPassword().toCharArray());
		}
		return options;
	}

	private MqttCallbackExtended callback() {
		return new MqttCallbackExtended() {
			@Override
			public void connectComplete(boolean reconnect, String serverURI) {
				LOGGER.info("Conexão MQTT {} com broker={}", reconnect ? "restabelecida" : "estabelecida", serverURI);
				if (reconnect) {
					resubscribe();
				}
			}

			@Override
			public void connectionLost(Throwable cause) {
				LOGGER.warn("Conexão MQTT perdida: {}", cause.getMessage(), cause);
			}

			@Override
			public void messageArrived(String topic, MqttMessage message) {
				// Messages are handled by the per-subscription listener.
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {
				// Subscriber client does not publish messages.
			}
		};
	}

	private void resubscribe() {
		try {
			if (client != null && client.isConnected()) {
				client.subscribe(properties.getTelemetryTopic(), QOS, leituraListener());
				client.subscribe(properties.getStatusTopic(), QOS, statusListener());
			}
		}
		catch (MqttException ex) {
			LOGGER.error("Falha ao reinscrever nos topics MQTT {} e {}: {}",
					properties.getTelemetryTopic(),
					properties.getStatusTopic(),
					ex.getMessage(),
					ex);
		}
	}

	private IMqttMessageListener leituraListener() {
		return this::handleTelemetryMessage;
	}

	private IMqttMessageListener statusListener() {
		return this::handleStatusMessage;
	}

	void handleTelemetryMessage(String topic, MqttMessage message) {
		String rawPayload = new String(message.getPayload(), StandardCharsets.UTF_8);
		try {
			LOGGER.info("Leitura MQTT recebida topic={} payload={}", topic, rawPayload);
			MqttLeituraPayload payload = normalizePayload(objectMapper.readValue(rawPayload, MqttLeituraPayload.class), topic);
			LeituraIotResponse leitura = leituraService.criar(toRequest(payload));
			LOGGER.info("Leitura MQTT salva idLeitura={} idRegiao={} codigoEstacao={}",
					leitura.idLeitura(),
					leitura.idRegiao(),
					payload.codigoEstacao());

			MqttComandoAlertaPayload comando = buildCommand(payload, leitura);
			comandoAlertaPublisher.publish(comando);
		}
		catch (Exception ex) {
			LOGGER.error("Erro ao processar leitura MQTT topic={} payload={}: {}",
					topic,
					rawPayload,
					ex.getMessage(),
					ex);
		}
	}

	void handleStatusMessage(String topic, MqttMessage message) {
		String rawPayload = new String(message.getPayload(), StandardCharsets.UTF_8);
		try {
			LOGGER.info("Status MQTT recebido topic={} payload={}", topic, rawPayload);
			MqttStatusPayload payload = normalizeStatusPayload(objectMapper.readValue(rawPayload, MqttStatusPayload.class), topic);
			statusService.registrar(payload);
			LOGGER.info("Status MQTT registrado stationCode={} uptimeSeg={} rssi={}",
					payload.stationCode(),
					payload.uptimeSeg(),
					payload.rssi());
		}
		catch (Exception ex) {
			LOGGER.error("Erro ao processar status MQTT topic={} payload={}: {}",
					topic,
					rawPayload,
					ex.getMessage(),
					ex);
		}
	}

	private LeituraIotCreateRequest toRequest(MqttLeituraPayload payload) {
		return new LeituraIotCreateRequest(
				payload.idEstacao(),
				payload.codigoEstacao(),
				payload.dtLeitura(),
				payload.distanciaAguaCm(),
				payload.nivelAguaPercentual(),
				payload.inclinacaoGraus(),
				payload.vibracao(),
				payload.pressaoHpa(),
				payload.pm25(),
				payload.pm10());
	}

	private MqttLeituraPayload normalizePayload(MqttLeituraPayload payload, String topic) {
		String codigoEstacao = payload.codigoEstacao();
		String codigoEstacaoTopic = extractCodigoEstacao(topic);
		if (!StringUtils.hasText(codigoEstacao)) {
			codigoEstacao = codigoEstacaoTopic;
		}
		else if (StringUtils.hasText(codigoEstacaoTopic) && !codigoEstacao.equals(codigoEstacaoTopic)) {
			LOGGER.warn("codigoEstacao do payload ({}) difere do topic MQTT ({})", codigoEstacao, codigoEstacaoTopic);
		}
		return new MqttLeituraPayload(
				payload.idEstacao(),
				codigoEstacao,
				payload.dtLeitura(),
				payload.distanciaAguaCm(),
				payload.nivelAguaPercentual(),
				payload.inclinacaoGraus(),
				payload.vibracao(),
				payload.pressaoHpa(),
				payload.pm25(),
				payload.pm10());
	}

	private MqttStatusPayload normalizeStatusPayload(MqttStatusPayload payload, String topic) {
		String stationCode = payload.stationCode();
		String stationCodeTopic = extractStationCode(topic, "status");
		if (!StringUtils.hasText(stationCode)) {
			stationCode = stationCodeTopic;
		}
		else if (StringUtils.hasText(stationCodeTopic) && !stationCode.equals(stationCodeTopic)) {
			LOGGER.warn("stationCode do status ({}) difere do topic MQTT ({})", stationCode, stationCodeTopic);
		}
		return new MqttStatusPayload(
				stationCode,
				payload.mac(),
				payload.uptimeSeg(),
				payload.rssi(),
				payload.ip(),
				payload.versaoFirmware());
	}

	private String extractCodigoEstacao(String topic) {
		return extractStationCode(topic, "telemetria");
	}

	private String extractStationCode(String topic, String expectedSuffix) {
		String[] parts = topic == null ? new String[0] : topic.split("/");
		if (parts.length >= 4 && "estacoes".equals(parts[1]) && expectedSuffix.equals(parts[3])) {
			return parts[2];
		}
		return null;
	}

	private MqttComandoAlertaPayload buildCommand(MqttLeituraPayload payload, LeituraIotResponse leitura) {
		if (!properties.isEvaluateRiskOnMessage()) {
			LOGGER.info("Avaliação de risco MQTT desabilitada para idRegiao={}", leitura.idRegiao());
			return comandoAlertaPayloadFactory.monitoringOnly(payload.codigoEstacao());
		}

		AvaliarRiscoResponse risco = riscoService.avaliar(leitura.idRegiao());
		MqttComandoAlertaPayload comando = comandoAlertaPayloadFactory.fromRisk(payload.codigoEstacao(), risco);
		LOGGER.info("Risco MQTT avaliado idRegiao={} nivelRisco={} tipoRisco={} score={} alertasGerados={}",
				leitura.idRegiao(),
				comando.nivelRisco(),
				comando.tipoRiscoPrincipal(),
				comando.score(),
				risco.alertas().size());
		return comando;
	}

	@Override
	public void destroy() throws Exception {
		mqttExecutor.shutdownNow();
		if (client != null && client.isConnected()) {
			client.disconnect();
			client.close();
		}
	}

}
