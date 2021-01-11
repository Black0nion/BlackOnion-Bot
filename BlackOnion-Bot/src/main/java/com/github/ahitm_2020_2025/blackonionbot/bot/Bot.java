package com.github.ahitm_2020_2025.blackonionbot.bot;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Scanner;

import com.github.ahitm_2020_2025.blackonionbot.DefaultValues;
import com.github.ahitm_2020_2025.blackonionbot.RestAPI.API;
import com.github.ahitm_2020_2025.blackonionbot.SQL.LiteSQL;
import com.github.ahitm_2020_2025.blackonionbot.SQL.SQLManager;
import com.github.ahitm_2020_2025.blackonionbot.commands.bot.ActivityCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.bot.ReloadCommand;
import com.github.ahitm_2020_2025.blackonionbot.commands.bot.StatusCommand;
import com.github.ahitm_2020_2025.blackonionbot.enums.RunMode;
import com.github.ahitm_2020_2025.blackonionbot.systems.BirthdaySystem;
import com.github.ahitm_2020_2025.blackonionbot.systems.HandRaiseSystem;
import com.github.ahitm_2020_2025.blackonionbot.systems.MessageLogSystem;
import com.github.ahitm_2020_2025.blackonionbot.systems.SelfRoleSystem;
import com.github.ahitm_2020_2025.blackonionbot.systems.language.LanguageSystem;
import com.github.ahitm_2020_2025.blackonionbot.utils.JarUtils;
import com.github.ahitm_2020_2025.blackonionbot.utils.ValueManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Bot extends ListenerAdapter {
	public static RunMode runMode;
	
	public static JDABuilder builder;

	public static ArrayList<String> notifyStatusUsers;
	
	public static JDA jda;

	public static int line_count = 0;
	
	public static boolean isJarFile = false;
	
	@SuppressWarnings("resource")
	public static void startBot() {
		System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
		System.setProperty("org.eclipse.jetty.LEVEL", "OFF");
		new File("files").mkdir();
		isJarFile = JarUtils.runningFromJar();
		
		new ValueManager();
		DefaultValues.init();
		BotInformation.init();
		
		LiteSQL.connect();
		SQLManager.onCreate();
		LanguageSystem.init();
		
		BotSecrets.init();
		builder = JDABuilder
				.createDefault(BotSecrets.bot_token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES,
						GatewayIntent.GUILD_MESSAGE_REACTIONS)
				.disableCache(EnumSet.of(CacheFlag.CLIENT_STATUS, CacheFlag.ACTIVITY, CacheFlag.EMOTE))
				.enableCache(CacheFlag.VOICE_STATE)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.enableIntents(GatewayIntent.GUILD_MEMBERS);

		EventWaiter waiter = new EventWaiter();
		
		builder.addEventListeners(new CommandBase(), new MessageLogSystem(), new Bot(), new SelfRoleSystem(), new HandRaiseSystem(), waiter);
		
		CommandBase.addCommands(waiter);
		MessageLogSystem.init();
		builder.setStatus(StatusCommand.getStatusFromFile());
		builder.setActivity(ActivityCommand.getActivity());
		try {
			builder.build();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[BOT] Failed to connect to the bot! Please make sure to have a file named \"token.ahitm\" with the bot's token in the files folder!");
			LiteSQL.disconnect();
			System.out.println("Terminating bot.");
		}
		
		BirthdaySystem.init();
		
		new API();
		
		while (true) {
			Scanner sc = new Scanner(System.in);
			String input = sc.nextLine();
			if (input.equalsIgnoreCase("reload")) {
				ReloadCommand.reload();
			}
		}
	}

	@Override
	public void onReady(ReadyEvent e) {
		System.out.println("Connected to " + e.getJDA().getSelfUser().getName() + "#" + e.getJDA().getSelfUser().getDiscriminator());
		jda = e.getJDA();
		Thread status = new Thread(new Runnable() {
			@Override
			public void run() {
				OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
				while (true) {
					try {
						String activityType = ValueManager.getString("activityType");
						if (activityType != null && (!activityType.equalsIgnoreCase("") && !(activityType.equalsIgnoreCase("none")))) {
							e.getJDA().getPresence().setActivity(ActivityCommand.getActivity());
							Thread.sleep(5000);
						}
						e.getJDA().getPresence().setActivity(Activity.listening("dem Prefix " + BotInformation.prefix));
						Thread.sleep(5000);
						e.getJDA().getPresence().setActivity(Activity.listening("dem OS " + os.getName()));
						Thread.sleep(5000);
						e.getJDA().getPresence().setActivity(Activity.listening("mit " + os.getAvailableProcessors() + " CPU Kernen"));
						Thread.sleep(5000);
						e.getJDA().getPresence().setActivity(Activity.listening(BotInformation.line_count + " Zeilen code in " + BotInformation.file_count + " Datein"));
						Thread.sleep(5000);
//						e.getJDA().getPresence().setActivity(Activity.listening("mit " + Utils.round("#.###", (double) getOsThings(os).get("getProcessCpuLoad")) + "% CPU Load"));
//						Thread.sleep(5000);
//						e.getJDA().getPresence().setActivity(Activity.listening("mit " + Utils.round("#.##", (double) getOsThings(os).get("getSystemCpuLoad")).substring(2) +"% insgesamtem CPU Load"));
//						Thread.sleep(5000);
					} catch (Exception e) {
						if (runMode == RunMode.PRODUCTION)
							e.printStackTrace();
					}
				}
			}
		});
		status.setName("Status");
		status.start();
		/*
		 * @Deprecated: not working due to not be able to message not cached users on Discord's side (intended)
			for (Guild g : e.getJDA().getGuilds()) {
				for (Member user : g.getMembers()) {
					if (notifyStatusUsers.contains(user.getUser().getId())) {
						user.getUser().openPrivateChannel().complete().sendMessage("I booted up!").queue();
					}
				}
			}
		*/
	}
	
	public static HashMap<String, Object> getOsThings(OperatingSystemMXBean os) {
		HashMap<String, Object> things = new HashMap<>();
		for (Method method : os.getClass().getDeclaredMethods()) {
		    method.setAccessible(true);
		    if (method.getName().startsWith("get")
		        && Modifier.isPublic(method.getModifiers())) {
		            Object value;
		        try {
		            value = method.invoke(os);
		        } catch (Exception e) {
		            value = e;
				}
				things.put(method.getName(), value);
			}
		}
		return things;
	}
}
