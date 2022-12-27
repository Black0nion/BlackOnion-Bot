package com.github.black0nion.blackonionbot.stats;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Prometheus {

	private static final Logger logger = LoggerFactory.getLogger(Prometheus.class);

	private final Config config;


	public Prometheus(Config config) {
		this.config = config;
		start();
	}

	@Reloadable("prometheus")
	private static void restart() {
		new Prometheus(Bot.getInstance().getConfig());
	}

	private void start() {
		logger.info("Initializing Prometheus...");
		try {
			// expose built in metrics for the hotspot JVM
			DefaultExports.initialize();

			new HTTPServer.Builder()
				.withPort(config.getPrometheusPort())
				.build();
			logger.info("Prometheus HTTP Server started on port {}", config.getPrometheusPort());
		} catch (IOException ex) {
			logger.error("Could not initialize Prometheus HTTP Server!", ex);
		}
	}
}
