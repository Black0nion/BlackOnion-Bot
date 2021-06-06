package com.github.black0nion.blackonionbot.influx;

import java.lang.management.ManagementFactory;
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
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearSystem;
import com.github.black0nion.blackonionbot.utils.CredentialsManager;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class InfluxManager {
	
	private static InfluxDBClient influxDB = null;
	
	private static final int mb = 1024 * 1024;
	
	public static boolean connect(final String databaseURL, final String token, final String org) {
		if (influxDB != null) influxDB.close();
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
			if (!connect(manager.getString("influx_database-url"), manager.getString("influx_token"), manager.getString("influx_org")))
				return;
			Bot.executor.submit(() -> testSaving(new Document().append("test", "moino")));
			
			try {
				Bot.jda.awaitReady();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			
			new Timer().scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					saveStats();
				}
			}, 0L, 10000L);
		});
	}
	
	public static void saveStats() {
		final Point point = Point.measurement("stats").time(System.currentTimeMillis(), WritePrecision.MS)
				.addField("cmdcount", CommandBase.commandsLastTenSecs)
				.addField("messagecount", CommandBase.messagesLastTenSecs)
				.addField("cpuload", getProcessCpuLoad())
				.addField("guildcount", getGuildCount())
				.addField("ramload", getProcessRamLoad())
				.addField("maxramload", getProcessMaxRamLoad())
				.addField("ping", Bot.jda.getGatewayPing())
				.addField("profanityfiltered", AntiSwearSystem.profanityFilteredLastTenSecs)
				.addField("running", true);
		CommandBase.commandsLastTenSecs = 0;
		CommandBase.messagesLastTenSecs = 0;
		AntiSwearSystem.profanityFilteredLastTenSecs = 0;
		influxDB.getWriteApi().writePoint(point);
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
	
	public static double getProcessCpuLoad() {
		try {
			    final MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
			    final ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
			    final AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });
		
			    if (list.isEmpty())     return Double.NaN;
		
			    final Attribute att = (Attribute)list.get(0);
			    final Double value  = (Double)att.getValue();
		
			    // usually takes a couple of seconds before we get real values
			    if (value == -1.0)      return Double.NaN;
			    // returns a percentage value with 1 decimal point precision
			    return Utils.roundToDouble("#0.000", value);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return Double.NaN;
	}
	
	private static double getProcessRamLoad() {
		final Runtime runtime = Runtime.getRuntime();
		return (runtime.totalMemory() - runtime.freeMemory()) / mb;
	}
	
	private static double getProcessMaxRamLoad() {
		final Runtime runtime = Runtime.getRuntime();
		return runtime.maxMemory() / mb;
	}
	
	public static int getGuildCount() {
		return Bot.jda.getGuilds().size();
	}
}