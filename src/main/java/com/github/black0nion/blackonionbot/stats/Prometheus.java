package com.github.black0nion.blackonionbot.stats;

import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.utils.config.Config;
import io.prometheus.client.exporter.HTTPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Prometheus {

	private static final Logger LOGGER = LoggerFactory.getLogger(Prometheus.class);

	@Reloadable("prometheus")
	public static void init() {
		try {
			new HTTPServer.Builder()
				.withPort(Config.prometheus_port)
				.build();
		} catch (IOException ex) {
			LOGGER.error("Could not initialize Prometheus HTTP Server!", ex);
		}
	}
}