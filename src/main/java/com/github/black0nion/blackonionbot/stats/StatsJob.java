package com.github.black0nion.blackonionbot.stats;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.config.featureflags.FeatureFlags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatsJob implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(StatsJob.class);
	private static final int FIVE_MINUTES_AS_ITERATIONS = (5 * 60) / StatisticsManager.DELAY_BETWEEN_COLLECTION;
	private final FeatureFlags featureFlags;

	private int iteration = 0;
	private boolean initialized = false;

	public StatsJob(FeatureFlags featureFlags) {
		this.featureFlags = featureFlags;
	}

	@Override
	public void run() {
		debug("Collecting statistics...");

		StatisticsManager.RAM_LOAD.labels(String.valueOf(StatisticsManager.getProcessMaxRamLoad())).set(StatisticsManager.getProcessRamLoad());
		StatisticsManager.CPU_LOAD.set(StatisticsManager.getProcessCpuLoad());

		// refresh every 5 minutes
		if (!initialized || iteration >= FIVE_MINUTES_AS_ITERATIONS) {
			if (shouldDebugLog() || featureFlags.stats_logCountRefresh.getValue())
				logger.debug("Hit iteration {}, refreshing guild and user count...", iteration);

			initialized = true;
			StatisticsManager.GUILD_COUNT.set(StatisticsManager.reloadGuildCount());
			StatisticsManager.USER_COUNT.set(StatisticsManager.reloadUserCount());
			iteration = 0;

			if (shouldDebugLog() || featureFlags.stats_logCountRefresh.getValue())
				logger.debug("Successfully refreshed guild count! Guilds: {}, Users: {}", StatisticsManager.GUILD_COUNT.get(), StatisticsManager.USER_COUNT.get());
		}

		// TODO: replace "StatisticsManager.STARTUP_TIME" with Bot's Startup Time
		StatisticsManager.UPTIME.set((System.currentTimeMillis() - StatisticsManager.STARTUP_TIME) / 1000F);
		StatisticsManager.PING.set(Bot.getInstance().getJDA().getGatewayPing());
		iteration++;

		debug("Successfully collected statistics!");
	}

	private void debug(String s) {
		if (shouldDebugLog())
			logger.debug(s);
	}

	private void debug(String s, Object... objs) {
		if (shouldDebugLog())
			logger.debug(s, objs);
	}

	private boolean shouldDebugLog() {
		return featureFlags.stats_logCollection.getValue() && logger.isDebugEnabled();
	}
}
