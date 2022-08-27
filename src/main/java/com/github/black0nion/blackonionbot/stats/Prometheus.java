package com.github.black0nion.blackonionbot.stats;

import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.config.api.Config;
import io.prometheus.client.exporter.HTTPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Prometheus {

	private static final Logger LOGGER = LoggerFactory.getLogger(Prometheus.class);

	private final Config config;

	private static Prometheus instance;

	public Prometheus(Config config) {
		instance = this;
		this.config = config;
		start();
	}

	@Reloadable("prometheus")
	private static void start() {
		LOGGER.info("Initializing Prometheus...");
		try {
			new HTTPServer.Builder()
				.withPort(instance.config.getPrometheusPort())
				.build();
			LOGGER.info("Prometheus HTTP Server started on port {}", instance.config.getPrometheusPort());
		} catch (IOException ex) {
			LOGGER.error("Could not initialize Prometheus HTTP Server!", ex);
		}
	}
}
