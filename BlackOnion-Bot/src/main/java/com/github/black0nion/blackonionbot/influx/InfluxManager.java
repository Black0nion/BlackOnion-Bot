package com.github.black0nion.blackonionbot.influx;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.bson.Document;
import org.jetbrains.annotations.TestOnly;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.enums.LogOrigin;
import com.github.black0nion.blackonionbot.utils.CredentialsManager;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class InfluxManager {
	
	private static InfluxDBClient influxDB = null;
	
	public static void connect(String databaseURL, String token, String org) {
		if (influxDB != null) influxDB.close();
		influxDB = InfluxDBClientFactory.create(databaseURL, token.toCharArray(), org, "BlackOnion-Bot");
		try { 
			//TODO: implement shit to make it throw shit
		} catch (Exception e) {
			e.printStackTrace();
			Logger.logError("Couldn't connect to InfluxDB!", LogOrigin.INFLUX_DB);
		    return;
		}
	}
	
	public static void init() {
		CredentialsManager manager = Bot.getCredentialsManager();
		connect(manager.getString("influx_database-url"), manager.getString("influx_token"), manager.getString("influx_org"));
		testSaving(new Document().append("test", "moino"));
		
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				saveCommandExecuted();
			}
		}, 0, 10000);
	
	}
	
	public static void saveCommandExecuted() {
		Point point = Point.measurement("stats").time(System.currentTimeMillis(), WritePrecision.MS).addField("cmdcount", CommandBase.commandsLastTenSecs);
		influxDB.getWriteApi().writePoint(point);
		CommandBase.commandsLastTenSecs = 0;
	}
	
	@TestOnly
	public static void testSaving(Document args) {
		//Point point = Point.measurement("stats").time(System.currentTimeMillis(), WritePrecision.MS).addFields(args);
		Random random = new Random();
		for (int i = 0; i < 20; i++) {
			Point point2 = Point.measurement("stats").time(System.currentTimeMillis(), WritePrecision.MS).addField("num", random.nextInt(32));
			influxDB.getWriteApi().writePoint(point2);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void save(String bucket, Document args) {
		influxDB.getWriteApi().writePoint(Point.measurement(bucket).time(System.currentTimeMillis(), WritePrecision.MS).addFields(args));
	}
}
