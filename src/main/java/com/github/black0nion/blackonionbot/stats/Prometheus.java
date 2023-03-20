package com.github.black0nion.blackonionbot.stats;

import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.systems.reload.Reloadable;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Prometheus implements Reloadable {

	private static final Logger logger = LoggerFactory.getLogger(Prometheus.class);

	private final Config config;
	private HTTPServer httpServer;

	public Prometheus(Config config) {
		this.config = config;
		start();
	}

	@Override
	public void reload() {
		stop();
		start();
	}

	private void start() {
		logger.info("Initializing Prometheus...");
		try {
			// expose built in metrics for the hotspot JVM
			DefaultExports.initialize();

			httpServer = new HTTPServer.Builder()
				.withPort(config.getPrometheusPort())
				.build();
			logger.info("Prometheus HTTP Server started on port {}", config.getPrometheusPort());
		} catch (IOException ex) {
			logger.error("Could not initialize Prometheus HTTP Server!", ex);
		}
	}

	private void stop() {
		if (httpServer != null) {
			logger.info("Stopping Prometheus HTTP Server...");
			httpServer.close();
			logger.info("Prometheus HTTP Server stopped!");
		}
	}
}
