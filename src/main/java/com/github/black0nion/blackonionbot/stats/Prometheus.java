package com.github.black0nion.blackonionbot.stats;

import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.config.api.Config;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Prometheus {

	private static final Logger LOGGER = LoggerFactory.getLogger(Prometheus.class);

	private final Config config;

	private static Prometheus instance;

	private HTTPServer server;

	public Prometheus(Config config) {
		if (instance != null) instance.server.close();
		instance = this;
		this.config = config;
		start();
	}

	@Reloadable("prometheus")
	private static void restart() {
		new Prometheus(instance.config);
	}

	private void start() {
		LOGGER.info("Initializing Prometheus...");
		try {
			// expose built in metrics for the hotspot JVM
			DefaultExports.initialize();
			server = new HTTPServer.Builder()
				.withPort(config.getPrometheusPort())
				.build();
			LOGGER.info("Prometheus HTTP Server started on port {}", config.getPrometheusPort());
		} catch (IOException ex) {
			LOGGER.error("Could not initialize Prometheus HTTP Server!", ex);
		}
	}
}
