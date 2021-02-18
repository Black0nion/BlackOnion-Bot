package com.github.black0nion.blackonionbot.influx;

import java.lang.management.ManagementFactory;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.bson.Document;
import org.jetbrains.annotations.TestOnly;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.enums.LogOrigin;
import com.github.black0nion.blackonionbot.systems.MessageLogSystem;
import com.github.black0nion.blackonionbot.utils.CredentialsManager;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.InfluxException;

public class InfluxManager {
	
	private static InfluxDBClient influxDB = null;
	
	private static final int mb = 1024 * 1024;
	
	public static boolean connect(String databaseURL, String token, String org) {
		if (influxDB != null) influxDB.close();
		influxDB = InfluxDBClientFactory.create(databaseURL, token.toCharArray(), org, "BlackOnion-Bot");
		try { 
			influxDB.getWriteApiBlocking().writePoint(Point.measurement("startupshutdown").addField("online", true));
			Logger.logInfo("Connected.", LogOrigin.INFLUX_DB);
			return true;
		} catch (InfluxException e) {
			Logger.logError("Couldn't connect to InfluxDB!", LogOrigin.INFLUX_DB);
			influxDB = null;
		    return false;
		}
	}
	
	public static void init() {
		try {
			Bot.jda.awaitReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		CredentialsManager manager = Bot.getCredentialsManager();
		if (!connect(manager.getString("influx_database-url"), manager.getString("influx_token"), manager.getString("influx_org")))
			return;
		Bot.executor.submit(() -> testSaving(new Document().append("test", "moino")));
		
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				saveStats();
			}
		}, 0L, 10000L);
	
	}
	
	public static void saveStats() {
		Point point = Point.measurement("stats").time(System.currentTimeMillis(), WritePrecision.MS)
				.addField("cmdcount", CommandBase.commandsLastTenSecs)
				.addField("messagecount", MessageLogSystem.messagesSentLastTenSecs)
				.addField("cpuload", getProcessCpuLoad())
				.addField("guildcount", getGuildCount())
				.addField("ramload", getProcessRamLoad())
				.addField("maxramload", getProcessMaxRamLoad());
		influxDB.getWriteApi().writePoint(point);
		CommandBase.commandsLastTenSecs = 0;
		MessageLogSystem.messagesSentLastTenSecs = 0;
	}
	
	
	@TestOnly
	public static void testSaving(Document args) {
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
	
	public static double getProcessCpuLoad() {
		try {
			    MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
			    ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
			    AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });
		
			    if (list.isEmpty())     return Double.NaN;
		
			    Attribute att = (Attribute)list.get(0);
			    Double value  = (Double)att.getValue();
		
			    // usually takes a couple of seconds before we get real values
			    if (value == -1.0)      return Double.NaN;
			    // returns a percentage value with 1 decimal point precision
			    return Utils.roundToDouble("#0.000", value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Double.NaN;
	}
	
	private static double getProcessRamLoad() {
		Runtime runtime = Runtime.getRuntime();
		return (runtime.totalMemory() - runtime.freeMemory()) / mb;
	}
	
	private static double getProcessMaxRamLoad() {
		Runtime runtime = Runtime.getRuntime();
		return runtime.maxMemory() / mb;
	}
	
	public static int getGuildCount() {
		return Bot.jda.getGuilds().size();
	}
}
