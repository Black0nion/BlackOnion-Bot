package com.github.black0nion.blackonionbot.bot;

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
import com.github.black0nion.blackonionbot.systems.docker.DockerManager;
import com.github.black0nion.blackonionbot.systems.giveaways.GiveawaySystem;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.logging.EventEndpoint;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.systems.news.Newssystem;
import com.github.black0nion.blackonionbot.systems.plugins.PluginSystem;
import com.github.black0nion.blackonionbot.utils.CatchLogs;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.github.black0nion.blackonionbot.utils.config.ConfigManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;
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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.*;

public class Bot extends ListenerAdapter {

	public static RunMode runMode;

	public static JDA jda;

	public static boolean isJarFile = false;

	public static final ExecutorService executor = Executors.newCachedThreadPool();

	public static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

	public static final long startTime = System.currentTimeMillis();

	public static final Random random = new Random();

	public static final PrintStream out = System.out;
	public static final PrintStream err = System.err;

	public static final List<String> launchArguments = new ArrayList<>();
	public static Gson gson = new GsonBuilder()
			.registerTypeAdapterFactory(RecordTypeAdapterFactory.DEFAULT)
			.create();

	public static void startBot(String[] args) throws IOException {
		launchArguments.addAll(Arrays.asList(args));
		Utils.printLogo();
		CatchLogs.init();
		ConfigManager.loadConfig();
		DockerManager.init();
		// if (true) return;
		runMode = Config.run_mode;
		isJarFile = Utils.runningFromJar();
		Logger.log(LogMode.INFORMATION, "Starting BlackOnion-Bot in " + runMode + " mode...");
		new File("files").mkdirs();

		MongoManager.connect(Config.mongo_connection_string, Config.mongo_timeout);

		final JDABuilder builder = JDABuilder.createDefault(Config.token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_REACTIONS).disableCache(EnumSet.of(CacheFlag.CLIENT_STATUS, CacheFlag.ACTIVITY, CacheFlag.EMOTE)).enableCache(CacheFlag.VOICE_STATE).setMemberCachePolicy(MemberCachePolicy.ALL).enableIntents(GatewayIntent.GUILD_MEMBERS);

		final EventWaiter waiter = new EventWaiter();

		builder.addEventListeners(new CommandBase(), new Bot(), new ReactionRoleSystem(), new HandRaiseSystem(), new JoinLeaveSystem(), new AutoRolesSystem(), new EventEndpoint(), waiter);

		LanguageSystem.init();
		CommandBase.addCommands(waiter);
		builder.setStatus(StatusCommand.getStatusFromConfig());
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

		scheduledExecutor.scheduleAtFixedRate(new Runnable() {
			private boolean wasLineCount = false;
			@Override
			public void run() {
				if (wasLineCount) {
					jda.getPresence().setActivity(ActivityCommand.getActivity());
					wasLineCount = false;
				} else {
					jda.getPresence().setActivity(Activity.listening(Config.metadata.lines_of_code() + " lines of code in " + Config.metadata.files() + " files"));
					wasLineCount = true;
				}
			}
		}, 0, 1, TimeUnit.MINUTES);
	}

	@Override
	public void onDisconnect(final DisconnectEvent event) {
		final CloseCode closeCode = event.getCloseCode();
		Logger.log(LogMode.FATAL, LogOrigin.BOT, "Disconnected from Discord! Code: " + (closeCode != null ? closeCode.name() + " = " + closeCode.getMeaning() : "NONE"));
	}

	@Override
	public void onReconnected(final @NotNull ReconnectedEvent event) {
		Logger.log(LogMode.INFORMATION, LogOrigin.BOT, "Reconnected to Discord.");
	}
}