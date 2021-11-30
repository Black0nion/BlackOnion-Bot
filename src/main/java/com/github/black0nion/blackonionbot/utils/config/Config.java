package com.github.black0nion.blackonionbot.utils.config;

import javax.annotation.Nonnull;

public class Config {

	private static Config config;

	public static Config getConfig() {
		return config != null ? config : new Config();
	}

	protected static void setConfig(Config newConfig) {
		config = newConfig;
		discord = config.discordConfig;
		influx = config.influxConfig;
		mongo = config.mongoConfig;
		api = config.apiConfig;
		other = config.otherConfig;
	}

	public Config() {
		config = this;
	}

	@Nonnull
	public static DiscordConfig discord = new DiscordConfig();
	@Nonnull
	public static InfluxConfig influx = new InfluxConfig();
	@Nonnull
	public static MongoConfig mongo = new MongoConfig();
	@Nonnull
	public static APIConfig api = new APIConfig();
	@Nonnull
	public static OtherConfig other = new OtherConfig();

	public DiscordConfig discordConfig = new DiscordConfig();
	public InfluxConfig influxConfig = new InfluxConfig();
	public MongoConfig mongoConfig = new MongoConfig();
	public APIConfig apiConfig = new APIConfig();
	public OtherConfig otherConfig = new OtherConfig();

	@Override
	public String toString() {
		return "Config {" +
                "\n	discordConfig: " + discordConfig +
                "\n	influxConfig: " + influxConfig +
                "\n	mongoConfig: " + mongoConfig +
                "\n	apiConfig: " + apiConfig +
                "\n	otherConfig: " + otherConfig +
                "\n}";
	}
}