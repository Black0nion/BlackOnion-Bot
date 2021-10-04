package com.github.black0nion.blackonionbot.influx;

import org.bson.Document;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.systems.logging.StatisticsManager;
import com.github.black0nion.blackonionbot.utils.CredentialsManager;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class InfluxManager {

    public static InfluxDBClient influxDB = null;

    public static boolean connect(final String databaseURL, final String token, final String org) {
	if (influxDB != null) {
	    writeApi.flush();
	    writeApi.close();
	    influxDB.close();
	}
	try {
	    influxDB = InfluxDBClientFactory.create(databaseURL, token.toCharArray(), org, "BlackOnion-Bot");
	    influxDB.getWriteApiBlocking().writePoint(Point.measurement("startupshutdown").addField("online", true));
	    writeApi = influxDB.getWriteApi();
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
	    final CredentialsManager manager = Bot.getCredentialsManager();
	    if (!(manager.has("influx_database-url") && manager.has("influx_token") && manager.has("influx_org"))) {
		Logger.logError("No credentials for InfluxDB.", LogOrigin.INFLUX_DB);
		return;
	    }
	    if (!connect(manager.getString("influx_database-url"), manager.getString("influx_token"), manager.getString("influx_org"))) return;

	    try {
		Bot.jda.awaitReady();
	    } catch (final InterruptedException e) {
		e.printStackTrace();
	    }
	    StatisticsManager.init();
	});
    }

    private static WriteApi writeApi = null;

    public static void save(final String bucket, final Document args) {
	if (influxDB != null && args != null && args.size() != 0) {
	    writeApi.writePoint(Point.measurement(bucket).time(System.currentTimeMillis(), WritePrecision.MS).addFields(args));
	}
    }
}