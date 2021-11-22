package com.github.black0nion.blackonionbot.bot;

import java.io.File;
import java.io.PrintStream;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import com.github.black0nion.blackonionbot.API.API;
import com.github.black0nion.blackonionbot.blackobjects.BlackArrayList;
import com.github.black0nion.blackonionbot.commands.admin.ActivityCommand;
import com.github.black0nion.blackonionbot.commands.admin.StatusCommand;
import com.github.black0nion.blackonionbot.influx.InfluxManager;
import com.github.black0nion.blackonionbot.misc.LogMode;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.misc.RunMode;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.github.black0nion.blackonionbot.systems.AutoRolesSystem;
import com.github.black0nion.blackonionbot.systems.HandRaiseSystem;
import com.github.black0nion.blackonionbot.systems.JoinLeaveSystem;
import com.github.black0nion.blackonionbot.systems.ReactionRoleSystem;
import com.github.black0nion.blackonionbot.systems.giveaways.GiveawaySystem;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.logging.EventEndpoint;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.systems.news.Newssystem;
import com.github.black0nion.blackonionbot.systems.plugins.PluginSystem;
import com.github.black0nion.blackonionbot.utils.*;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import javassist.Modifier;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.CloseCode;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Bot extends ListenerAdapter {
    public static RunMode runMode;

    public static ArrayList<String> notifyStatusUsers;

    public static JDA jda;

    public static boolean isJarFile = false;

    private static CredentialsManager credentialsManager;

    public static final ExecutorService executor = Executors.newCachedThreadPool();

    public static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

    public static final long startTime = System.currentTimeMillis();

    public static final Random random = new Random();

    public static Future<Object> switchingStatusFuture;

    public static Callable<Object> switchingStatusCallable;

    public static final PrintStream out = System.out;
    public static final PrintStream err = System.err;

    @SuppressWarnings("resource")
    public void startBot() {
		Utils.printLogo();
		CatchLogs.init();
		new ValueManager();
		DefaultValues.init();
		isJarFile = Utils.runningFromJar();
		Logger.log(LogMode.INFORMATION, "Starting BlackOnion-Bot in " + runMode + " mode...");
		new File("files").mkdirs();

		credentialsManager = new CredentialsManager(runMode.name().toLowerCase());

		if (credentialsManager.has("mongo_connection_string")) {
			MongoManager.connect(credentialsManager.getString("mongo_connection_string"));
		} else {
			MongoManager.connect(credentialsManager.getString("mongo_ip"), credentialsManager.getString("mongo_port"), credentialsManager.getString("mongo_authdb"), credentialsManager.getString("mongo_username"), credentialsManager.getString("mongo_password"), credentialsManager.getInt("mongo_timeout"));
		}

		final JDABuilder builder = JDABuilder.createDefault(credentialsManager.getString("token"), GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_REACTIONS).disableCache(EnumSet.of(CacheFlag.CLIENT_STATUS, CacheFlag.ACTIVITY, CacheFlag.EMOTE)).enableCache(CacheFlag.VOICE_STATE).setMemberCachePolicy(MemberCachePolicy.ALL).enableIntents(GatewayIntent.GUILD_MEMBERS);

		final EventWaiter waiter = new EventWaiter();

		builder.addEventListeners(new CommandBase(), new Bot(), new ReactionRoleSystem(), new HandRaiseSystem(), new JoinLeaveSystem(), new AutoRolesSystem(), new EventEndpoint(), waiter);

		LanguageSystem.init();
		CommandBase.addCommands(waiter);
		builder.setStatus(StatusCommand.getStatusFromFile());
		builder.setActivity(ActivityCommand.getActivity());
		builder.setMaxReconnectDelay(32);
		try {
			jda = builder.build();
		} catch (final Exception e) {
			e.printStackTrace();
			Logger.log(LogMode.FATAL, LogOrigin.BOT, "Failed to connect to the bot! Please make sure to have the token saved in the file \"credentials." + runMode.name() + ".json\" in the folder \"files\" with the bot's token saved under the key \"token\"!");
			Logger.log(LogMode.ERROR, LogOrigin.BOT, "Terminating bot.");
			System.exit(-1);
		}

		BlackArrayList<Runnable> runnables = new BlackArrayList<>();
		runnables.addAndGetSelf(InfluxManager::init)
				.addAndGetSelf(BotInformation::init)
				.addAndGetSelf(GiveawaySystem::init)
				.addAndGetSelf(PlayerManager::init)
				.addAndGetSelf(Newssystem::init)
				.addAndGetSelf(API::init)
				.addAndGetSelf(PluginSystem::loadPlugins)
				.addAndGetSelf(ConsoleCommands::run);
		ExecutorService asyncStartup = Executors.newFixedThreadPool(runnables.size());
		runnables.forEach(asyncStartup::submit);
	}

    @Override
    public void onReady(final ReadyEvent e) {
	BotInformation.SELF_USER_ID = e.getJDA().getSelfUser().getIdLong();
	Logger.log(LogMode.INFORMATION, LogOrigin.BOT, "Connected to " + e.getJDA().getSelfUser().getName() + "#" + e.getJDA().getSelfUser().getDiscriminator() + " in " + (System.currentTimeMillis() - Bot.startTime) + "ms.");

	switchingStatusCallable = () -> {
	    while (true) {
		try {
		    final String activityType = ValueManager.getString("activityType");
		    if (activityType != null && (!activityType.equalsIgnoreCase("") && !(activityType.equalsIgnoreCase("none")))) {
			jda.getPresence().setActivity(ActivityCommand.getActivity());
			Thread.sleep(60000);
		    }
		    jda.getPresence().setActivity(Activity.listening(BotInformation.LINE_COUNT + " lines of code in " + BotInformation.FILE_COUNT + " files"));
		    Thread.sleep(60000);
		} catch (final Exception ex) {
		    if (!(ex instanceof InterruptedException)) {
			ex.printStackTrace();
		    }
		}
	    }
	};

	restartSwitchingStatus(e.getJDA());
	/**
	 * @Deprecated: not working due to not be able to message not cached users on
	 *              Discord's side (intended) notifyStatusUsers.forEach(userId -> {
	 *              jda.retrieveUserById(userId).queue(user -> {
	 *              user.openPrivateChannel().queue(channel -> {
	 *              channel.sendMessage("I booted up!").queue(); }); }); });
	 */
    }

    public static void restartSwitchingStatus(final JDA jda) {
	if (switchingStatusFuture != null) {
	    switchingStatusFuture.cancel(true);
	}
	switchingStatusFuture = executor.submit(switchingStatusCallable);
    }

    public static HashMap<String, Object> getOsThings(final OperatingSystemMXBean os) {
	final HashMap<String, Object> things = new HashMap<>();
	for (final Method method : os.getClass().getDeclaredMethods()) {
	    method.setAccessible(true);
	    if (method.getName().startsWith("get") && Modifier.isPublic(method.getModifiers())) {
		Object value;
		try {
		    value = method.invoke(os);
		} catch (final Exception e) {
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

    @Override
    public void onDisconnect(final DisconnectEvent event) {
	final CloseCode closeCode = event.getCloseCode();
	Logger.log(LogMode.FATAL, LogOrigin.BOT, "Disconnected from Discord! Code: " + (closeCode != null ? closeCode.name() + " = " + closeCode.getMeaning() : "NONE"));
    }

    @Override
    public void onReconnected(final ReconnectedEvent event) {
	Logger.log(LogMode.INFORMATION, LogOrigin.BOT, "Reconnected to Discord.");
    }
}