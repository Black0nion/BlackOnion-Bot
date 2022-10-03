package com.github.black0nion.blackonionbot.stats;

import org.eclipse.jetty.server.handler.StatisticsHandler;

import java.util.function.Consumer;

public class StatsCollectorFactory {

	private final Consumer<StatisticsHandler> consumer;

	public StatsCollectorFactory(Consumer<StatisticsHandler> consumer) {
		this.consumer = consumer;
	}

	private boolean initialized = false;

	public final void init(StatisticsHandler statsHandler) {
		if (!initialized) {
			initialized = true;
			consumer.accept(statsHandler);
		}
	}
}
