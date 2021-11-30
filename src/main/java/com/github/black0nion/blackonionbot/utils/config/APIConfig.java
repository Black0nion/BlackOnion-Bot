package com.github.black0nion.blackonionbot.utils.config;

public class APIConfig {
	public final int PORT = -1;
	public final boolean LOG_HEARTBEATS = false;

	@Override
	public String toString() {
		return "APIConfig{" +
                "PORT=" + PORT +
                ", LOG_HEARTBEATS=" + LOG_HEARTBEATS +
                '}';
	}
}