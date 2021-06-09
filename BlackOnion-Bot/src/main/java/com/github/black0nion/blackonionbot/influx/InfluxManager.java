package com.github.black0nion.blackonionbot.influx;

import org.bson.Document;
import org.jetbrains.annotations.TestOnly;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.systems.logging.StatisticsManager;
import com.github.black0nion.blackonionbot.utils.CredentialsManager;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class InfluxManager {

    public static InfluxDBClient influxDB = null;

    public static boolean connect(final String databaseURL, final String token, final String org) {
	if (influxDB != null) {
	    influxDB.close();
	}
	try {
	    influxDB = InfluxDBClientFactory.create(databaseURL, token.toCharArray(), org, "BlackOnion-Bot");
	    influxDB.getWriteApiBlocking().writePoint(Point.measurement("startupshutdown").addField("online", true));
	    Logger.logInfo("Connected.", LogOrigin.INFLUX_DB);
	    return true;
	} catch (final Exception e) {
	    Logger.logError("Couldn't connect to InfluxDB!", LogOrigin.INFLUX_DB);
	    influxDB = null;
	    return false;
	}
    }

    public static void init() {
	Bot.executor.submit(() -> {
	    final CredentialsManager manager = Bot.getCredentialsManager();
	    if (!connect(manager.getString("influx_database-url"), manager.getString("influx_token"), manager.getString("influx_org"))) return;
	    Bot.executor.submit(() -> testSaving(new Document().append("test", "moino")));

	    try {
		Bot.jda.awaitReady();
	    } catch (final InterruptedException e) {
		e.printStackTrace();
	    }
	    StatisticsManager.init();
	});
    }

    @TestOnly
    public static void testSaving(final Document args) {
	for (int i = 0; i < 20; i++) {
	    final Point point2 = Point.measurement("stats").time(System.currentTimeMillis(), WritePrecision.MS).addField("num", Bot.random.nextInt(32));
	    influxDB.getWriteApi().writePoint(point2);
	    try {
		Thread.sleep(1000);
	    } catch (final InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }

    public static void save(final String bucket, final Document args) {
	influxDB.getWriteApi().writePoint(Point.measurement(bucket).time(System.currentTimeMillis(), WritePrecision.MS).addFields(args));
    }
}