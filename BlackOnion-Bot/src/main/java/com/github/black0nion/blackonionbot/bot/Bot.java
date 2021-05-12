package com.github.black0nion.blackonionbot.bot;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.black0nion.blackonionbot.DefaultValues;
import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.RestAPI.API;
import com.github.black0nion.blackonionbot.commands.PrefixInfo;
import com.github.black0nion.blackonionbot.commands.bot.ActivityCommand;
import com.github.black0nion.blackonionbot.commands.bot.ReloadCommand;
import com.github.black0nion.blackonionbot.commands.bot.StatusCommand;
import com.github.black0nion.blackonionbot.influx.InfluxManager;
import com.github.black0nion.blackonionbot.misc.LogMode;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.misc.RunMode;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.github.black0nion.blackonionbot.systems.AutoRolesSystem;
import com.github.black0nion.blackonionbot.systems.BirthdaySystem;
import com.github.black0nion.blackonionbot.systems.HandRaiseSystem;
import com.github.black0nion.blackonionbot.systems.JoinLeaveSystem;
import com.github.black0nion.blackonionbot.systems.MessageLogSystem;
import com.github.black0nion.blackonionbot.systems.ReactionRoleSystem;
import com.github.black0nion.blackonionbot.systems.ToggleAPI;
import com.github.black0nion.blackonionbot.systems.dashboard.SessionManager;
import com.github.black0nion.blackonionbot.systems.giveaways.GiveawaysSystem;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.CredentialsManager;
import com.github.black0nion.blackonionbot.utils.CustomManager;
import com.github.black0nion.blackonionbot.utils.JarUtils;
import com.github.black0nion.blackonionbot.utils.ValueManager;
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
	
	private static CredentialsManager credentialsManager;
	
	public static final ExecutorService executor = Executors.newCachedThreadPool();
	
	public static final long startTime = System.currentTimeMillis();
	
	@SuppressWarnings("resource")
	public void startBot() {
		System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
		System.setProperty("org.eclipse.jetty.LEVEL", "OFF");
		new File("files").mkdir();
		isJarFile = JarUtils.runningFromJar();
		
		new ValueManager();
		DefaultValues.init();
		credentialsManager = new CredentialsManager(runMode.name().toLowerCase());
		
		BotSecrets.init();
		CustomManager mongoManager = new CustomManager("mongodb");
		if (mongoManager.getString("connection_string") != null)
			MongoManager.connect(mongoManager.getString("connection_string"));
		else
			MongoManager.connect(mongoManager.getString("ip"), mongoManager.getString("port"), mongoManager.getString("authdb"), mongoManager.getString("username"), mongoManager.getString("password"), mongoManager.getInt("timeout"));
		
		builder = JDABuilder
				.createDefault(BotSecrets.bot_token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES,
						GatewayIntent.GUILD_MESSAGE_REACTIONS)
				.disableCache(EnumSet.of(CacheFlag.CLIENT_STATUS, CacheFlag.ACTIVITY, CacheFlag.EMOTE))
				.enableCache(CacheFlag.VOICE_STATE)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.enableIntents(GatewayIntent.GUILD_MEMBERS);

		EventWaiter waiter = new EventWaiter();
		
		builder.addEventListeners(new CommandBase(), new MessageLogSystem(), new Bot(), new ReactionRoleSystem(), new HandRaiseSystem(), new JoinLeaveSystem(), new AutoRolesSystem(), new PrefixInfo(), waiter);
		
		LanguageSystem.init();
		CommandBase.addCommands(waiter);
		MessageLogSystem.init();
		builder.setStatus(StatusCommand.getStatusFromFile());
		builder.setActivity(ActivityCommand.getActivity());
		try {
			jda = builder.build();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[BOT] Failed to connect to the bot! Please make sure to have a file named \"token.ahitm\" with the bot's token in the files folder!");
			System.out.println("Terminating bot.");
			System.exit(-1);
		}

		InfluxManager.init();
		GuildManager.init();
		BotInformation.init();
		BirthdaySystem.init();
		GiveawaysSystem.init();
		PlayerManager.init();
		ToggleAPI.init();
		//MusicSystem.init();
		
		new API();

		SessionManager.init();
		
		Executors.newCachedThreadPool().submit(() -> { 
			while (true) {
				Scanner sc = new Scanner(System.in);
				String input = sc.nextLine();
				if (input.equalsIgnoreCase("reload")) {
					ReloadCommand.reload();
				}
			}
		});
	}

	@Override
	public void onReady(ReadyEvent e) {
		BotInformation.botId = e.getJDA().getSelfUser().getIdLong();
		Logger.log(LogMode.INFORMATION, LogOrigin.BOT, "Connected to " + e.getJDA().getSelfUser().getName() + "#" + e.getJDA().getSelfUser().getDiscriminator());
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
						e.getJDA().getPresence().setActivity(Activity.listening("the prefix " + BotInformation.defaultPrefix));
						Thread.sleep(5000);
						e.getJDA().getPresence().setActivity(Activity.listening("the OS " + os.getName()));
						Thread.sleep(5000);
						e.getJDA().getPresence().setActivity(Activity.listening("with " + os.getAvailableProcessors() + " CPU cores"));
						Thread.sleep(5000);
						e.getJDA().getPresence().setActivity(Activity.listening(BotInformation.line_count + " lines of code in " + BotInformation.file_count + " files"));
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
	
	public static CredentialsManager getCredentialsManager() {
		return credentialsManager;
	}
}
