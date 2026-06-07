package br.com.fiap.amanaje.leituras.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "amanaje.leituras.http")
public class LeituraHttpProperties {

	private boolean evaluateRisk = true;

	public boolean isEvaluateRisk() {
		return evaluateRisk;
	}

	public void setEvaluateRisk(boolean evaluateRisk) {
		this.evaluateRisk = evaluateRisk;
	}

}
