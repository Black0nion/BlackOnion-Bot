package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.api.API;
import com.github.black0nion.blackonionbot.commands.admin.ActivityCommand;
import com.github.black0nion.blackonionbot.commands.admin.ReloadCommand;
import com.github.black0nion.blackonionbot.commands.admin.StatusCommand;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.github.black0nion.blackonionbot.stats.Prometheus;
import com.github.black0nion.blackonionbot.stats.StatisticsManager;
import com.github.black0nion.blackonionbot.stats.StatsJob;
import com.github.black0nion.blackonionbot.systems.AutoRolesSystem;
import com.github.black0nion.blackonionbot.systems.JoinLeaveSystem;
import com.github.black0nion.blackonionbot.systems.ReactionRoleSystem;
import com.github.black0nion.blackonionbot.systems.docker.DockerManager;
import com.github.black0nion.blackonionbot.systems.giveaways.GiveawaySystem;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.systems.news.Newssystem;
import com.github.black0nion.blackonionbot.systems.plugins.PluginSystem;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.github.black0nion.blackonionbot.utils.config.ConfigManager;
import com.github.black0nion.blackonionbot.wrappers.ChainableArrayList;
import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.Paginator;
import com.github.ygimenez.model.PaginatorBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.CloseCode;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot extends ListenerAdapter {

	public static JDA jda;

	public static final ExecutorService executor = Executors.newCachedThreadPool();
	public static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

	public static final Random RANDOM = new Random();

	public static final List<String> launchArguments = new ArrayList<>();
	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapterFactory(RecordTypeAdapterFactory.DEFAULT)
			.create();

	public static final EventWaiter EVENT_WAITER = new EventWaiter();

	private static final Logger logger = LoggerFactory.getLogger(Bot.class);

	public static void startBot(String[] args) throws IOException {
		launchArguments.addAll(Arrays.asList(args));
		Utils.printLogo();
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		ConfigManager.loadConfig();
		LoggerFactory.getLogger(Bot.class).debug("Weep Woop debug message...");
		DockerManager.init();
		logger.info("Starting BlackOnion-Bot in " + Config.run_mode + " mode...");
		//noinspection ResultOfMethodCallIgnored
		new File("files").mkdirs();

		MongoManager.connect(Config.mongo_connection_string);

		final JDABuilder builder = JDABuilder.createDefault(Config.token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
			.disableCache(EnumSet.of(CacheFlag.CLIENT_STATUS, CacheFlag.ACTIVITY, CacheFlag.EMOTE))
			.enableCache(CacheFlag.VOICE_STATE)
			.setMemberCachePolicy(MemberCachePolicy.ALL)
			.enableIntents(GatewayIntent.GUILD_MEMBERS)
			.setMaxReconnectDelay(32)
			.addEventListeners(new CommandBase(), new SlashCommandBase(), new Bot(), new ReactionRoleSystem(), new JoinLeaveSystem(), new AutoRolesSystem(), new StatisticsManager(), EVENT_WAITER);

		LanguageSystem.init();
		// the constructor already needs the initialized hashmap
		ReloadCommand.initReloadableMethods();
		CommandBase.addCommands();
		SlashCommandBase.addCommands();
		builder.setStatus(StatusCommand.getStatusFromConfig());
		builder.setActivity(ActivityCommand.getActivity());

		try {
			jda = builder.build();
		} catch (final Exception e) {
			e.printStackTrace();
			logger.error("Failed to connect to the bot! Please make sure to provide the token correctly in either the environment variables or the .env file.");
			logger.error("Terminating bot.");
			System.exit(-1);
		}

		try {
			Paginator paginator = PaginatorBuilder.createPaginator()
				.setHandler(jda)
				.shouldEventLock(true)
				.setDeleteOnCancel(true)
				.shouldRemoveOnReact(true)
				.build();
			Pages.activate(paginator);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		ChainableArrayList<Runnable> runnables = new ChainableArrayList<>();
		runnables.addAndGetSelf(BotInformation::init)
				.addAndGetSelf(GiveawaySystem::init)
				.addAndGetSelf(PlayerManager::init)
				.addAndGetSelf(Newssystem::init)
				.addAndGetSelf(API::init)
				.addAndGetSelf(PluginSystem::loadPlugins)
				.addAndGetSelf(ConsoleCommands::run)
				.addAndGetSelf(Prometheus::init);
		ExecutorService asyncStartup = Executors.newFixedThreadPool(runnables.size());
		runnables.forEach(asyncStartup::submit);

		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new StatsJob(), 0, 15, TimeUnit.SECONDS);

		Runtime.getRuntime().addShutdownHook(new Thread(MongoManager::disconnect));
	}

	@Override
	public void onReady(final ReadyEvent e) {
		final JDA jda = e.getJDA();
		BotInformation.SELF_USER_ID = jda.getSelfUser().getIdLong();
		logger.info("Connected to " + jda.getSelfUser().getName() + "#" + jda.getSelfUser().getDiscriminator() + " in " + (System.currentTimeMillis() - StatisticsManager.STARTUP_TIME) + "ms.");

		jda.getPresence().setActivity(ActivityCommand.getActivity());

		SlashCommandBase.updateCommandsDev(jda);
	}

	@Reloadable("commands")
	public static void updateCommands() {
		CommandBase.addCommands();
		SlashCommandBase.addCommands();
	}

	@Override
	public void onDisconnect(final DisconnectEvent event) {
		final CloseCode closeCode = event.getCloseCode();
		logger.error("Disconnected from Discord! Code: " + (closeCode != null ? closeCode.name() + " = " + closeCode.getMeaning() : "NONE"));
	}

	@Override
	public void onReconnected(final @NotNull ReconnectedEvent event) {
		logger.info("Reconnected to Discord.");
	}
}