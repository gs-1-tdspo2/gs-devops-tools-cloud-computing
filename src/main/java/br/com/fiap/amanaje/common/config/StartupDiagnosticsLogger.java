package br.com.fiap.amanaje.common.config;

import java.util.Arrays;
import java.util.stream.Collectors;

import br.com.fiap.amanaje.leituras.config.LeituraHttpProperties;
import br.com.fiap.amanaje.leituras.mqtt.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupDiagnosticsLogger
		implements ApplicationRunner, ApplicationListener<ApplicationReadyEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartupDiagnosticsLogger.class);

	private final Environment environment;
	private final AmanajeCorsProperties corsProperties;
	private final MqttProperties mqttProperties;
	private final LeituraHttpProperties leituraHttpProperties;
	private final String serverPort;
	private final String datasourceUrl;

	public StartupDiagnosticsLogger(
			Environment environment,
			AmanajeCorsProperties corsProperties,
			MqttProperties mqttProperties,
			LeituraHttpProperties leituraHttpProperties,
			@Value("${server.port}") String serverPort,
			@Value("${spring.datasource.url:}") String datasourceUrl) {
		this.environment = environment;
		this.corsProperties = corsProperties;
		this.mqttProperties = mqttProperties;
		this.leituraHttpProperties = leituraHttpProperties;
		this.serverPort = serverPort;
		this.datasourceUrl = datasourceUrl;
	}

	@Override
	public void run(ApplicationArguments args) {
		LOGGER.info("Amanajé startup config profiles={} serverPort={} corsAllowedOrigins={}",
				activeProfiles(),
				serverPort,
				corsProperties.getAllowedOrigins());
		LOGGER.info("Amanajé datasource config url={} usernameConfigured={} oracleConnectTimeoutMs={} oracleReadTimeoutMs={}",
				maskDatasourceUrl(datasourceUrl),
				hasText(environment.getProperty("spring.datasource.username")),
				environment.getProperty("spring.datasource.hikari.data-source-properties.oracle.net.CONNECT_TIMEOUT"),
				environment.getProperty("spring.datasource.hikari.data-source-properties.oracle.jdbc.ReadTimeout"));
		LOGGER.info("Amanajé telemetry config httpEvaluateRisk={} mqttEnabled={} mqttBroker={} mqttClientId={} mqttTelemetryTopic={} mqttStatusTopic={} mqttEvaluateRisk={} mqttConnectTimeoutSeconds={} mqttKeepAliveSeconds={}",
				leituraHttpProperties.isEvaluateRisk(),
				mqttProperties.isEnabled(),
				mqttProperties.getBrokerUrl(),
				mqttProperties.getClientId(),
				mqttProperties.getTelemetryTopic(),
				mqttProperties.getStatusTopic(),
				mqttProperties.isEvaluateRiskOnMessage(),
				mqttProperties.getConnectionTimeoutSeconds(),
				mqttProperties.getKeepAliveIntervalSeconds());
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		LOGGER.info("Amanajé API pronta para receber requisições REST.");
	}

	private String activeProfiles() {
		String[] profiles = environment.getActiveProfiles();
		if (profiles.length == 0) {
			return "default";
		}
		return Arrays.stream(profiles).collect(Collectors.joining(","));
	}

	private String maskDatasourceUrl(String url) {
		if (!hasText(url)) {
			return "";
		}
		int atIndex = url.indexOf('@');
		if (atIndex < 0) {
			return url;
		}
		int endIndex = url.indexOf('?', atIndex);
		String suffix = endIndex < 0 ? "" : url.substring(endIndex);
		return url.substring(0, atIndex + 1) + "***" + suffix;
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

}
