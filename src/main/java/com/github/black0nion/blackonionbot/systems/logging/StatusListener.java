package com.github.black0nion.blackonionbot.systems.logging;

import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.status.Status;

import java.util.List;

/**
 * <a href="https://stackoverflow.com/a/61125380/10052779">Taken from StackOverflow</a>
 * Required to make the initialization logs print <i>before</i> the application logs
 */
public class StatusListener extends OnConsoleStatusListener {

	private static final int LOG_LEVEL = Status.WARN;

	@Override
	public void addStatusEvent(Status status) {
		if (status.getLevel() == LOG_LEVEL) {
			super.addStatusEvent(status);
		}
	}

	@Override
	public void start() {
		final List<Status> statuses = context.getStatusManager().getCopyOfStatusList();
		for (Status status : statuses) {
			if (status.getLevel() == LOG_LEVEL) {
				super.start();
			}
		}
	}
}
