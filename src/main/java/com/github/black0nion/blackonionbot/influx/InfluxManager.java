package com.github.black0nion.blackonionbot.influx;

import com.github.black0nion.blackonionbot.utils.config.Config;
import org.bson.Document;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.systems.logging.StatisticsManager;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class InfluxManager {

	public static InfluxDBClient influxDB = null;
	private static WriteApi writeApi = null;

	public static boolean connect(final String databaseURL, final String token, final String org) {
		if (influxDB != null) {
			writeApi.flush();
			writeApi.close();
			influxDB.close();
		}
		try {
			influxDB = InfluxDBClientFactory.create(databaseURL, token.toCharArray(), org, "BlackOnion-Bot");
			influxDB.getWriteApiBlocking().writePoint(Point.measurement("startupshutdown").addField("online", true));
			writeApi = influxDB.makeWriteApi();
			Logger.logInfo("Connected.", LogOrigin.INFLUX_DB);
			return true;
		} catch (final Exception e) {
			Logger.logError("Couldn't connect to InfluxDB! Error: " + e.getMessage(), LogOrigin.INFLUX_DB);
			influxDB = null;
			return false;
		}
	}

	@Reloadable("influxdb")
	public static void init() {
		Bot.executor.submit(() -> {
			if (Config.influx.DATABASE_URL == null || Config.influx.TOKEN == null || Config.influx.ORG == null) {
				Logger.logError("No credentials for InfluxDB.", LogOrigin.INFLUX_DB);
				return;
			}
			if (!connect(Config.influx.DATABASE_URL, Config.influx.TOKEN, Config.influx.ORG)) return;

			try {
				Bot.jda.awaitReady();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			StatisticsManager.init();
		});
	}

	public static void save(final String bucket, final Document args) {
		if (influxDB != null && args != null && args.size() != 0) {
			writeApi.writePoint(Point.measurement(bucket).time(System.currentTimeMillis(), WritePrecision.MS).addFields(args));
		}
	}
}