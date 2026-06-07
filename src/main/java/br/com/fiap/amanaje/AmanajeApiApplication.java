package br.com.fiap.amanaje;

import br.com.fiap.amanaje.common.config.AmanajeCorsProperties;
import br.com.fiap.amanaje.leituras.config.LeituraHttpProperties;
import br.com.fiap.amanaje.leituras.mqtt.MqttProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ AmanajeCorsProperties.class, MqttProperties.class, LeituraHttpProperties.class })
public class AmanajeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmanajeApiApplication.class, args);
	}

}
