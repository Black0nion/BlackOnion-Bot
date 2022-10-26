package com.github.black0nion.blackonionbot.stats;

import com.github.black0nion.blackonionbot.bot.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatsJob implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(StatsJob.class);
	private static final int FIVE_MINUTES_AS_ITERATIONS = 300 / StatisticsManager.DELAY_BETWEEN_COLLECTION;

	private int iteration = 0;
	private boolean initialized = false;

	@Override
	public void run() {
		logger.debug("Collecting statistics...");
		StatisticsManager.RAM_LOAD.labels(String.valueOf(StatisticsManager.getProcessMaxRamLoad())).set(StatisticsManager.getProcessRamLoad());
		StatisticsManager.CPU_LOAD.set(StatisticsManager.getProcessCpuLoad());
		// refresh every 5 minutes
		if (!initialized || iteration >= FIVE_MINUTES_AS_ITERATIONS) {
			logger.debug("Hit iteration {}, refreshing guild and user count...", iteration);
			initialized = true;
			StatisticsManager.GUILD_COUNT.set(StatisticsManager.reloadGuildCount());
			StatisticsManager.USER_COUNT.set(StatisticsManager.reloadUserCount());
			iteration = 0;
			logger.debug("Successfully refreshed guild and user count!");
		}
		StatisticsManager.UPTIME.set((System.currentTimeMillis() - StatisticsManager.STARTUP_TIME) / 1000F);
		StatisticsManager.PING.set(Bot.getInstance().getJDA().getGatewayPing());
		iteration++;
		logger.debug("Successfully collected statistics!");
	}
}
