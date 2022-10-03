package com.github.black0nion.blackonionbot.stats;

import com.github.black0nion.blackonionbot.bot.Bot;

public class StatsJob implements Runnable {
	@Override
	public void run() {
		StatisticsManager.RAM_LOAD.labels(String.valueOf(StatisticsManager.getProcessMaxRamLoad())).set(StatisticsManager.getProcessRamLoad());
		StatisticsManager.CPU_LOAD.set(StatisticsManager.getProcessCpuLoad());
		StatisticsManager.GUILD_COUNT.set(StatisticsManager.reloadGuildCount());
		StatisticsManager.USER_COUNT.set(StatisticsManager.reloadUserCount());
		StatisticsManager.UPTIME.set((System.currentTimeMillis() - StatisticsManager.STARTUP_TIME) / 1000F);
		StatisticsManager.PING.set(Bot.getInstance().getJDA().getGatewayPing());
	}
}