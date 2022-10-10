package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.commands.admin.ActivityCommand;
import com.github.black0nion.blackonionbot.commands.admin.ReloadCommand;
import com.github.black0nion.blackonionbot.commands.admin.StatusCommand;
import com.github.black0nion.blackonionbot.config.ConfigFileLoader;
import com.github.black0nion.blackonionbot.config.api.Config;
import com.github.black0nion.blackonionbot.config.impl.ConfigImpl;
import com.github.black0nion.blackonionbot.config.impl.ConfigLoaderImpl;
import com.github.black0nion.blackonionbot.inject.DefaultInjector;
import com.github.black0nion.blackonionbot.inject.Injector;
import com.github.black0nion.blackonionbot.inject.InjectorMap;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.github.black0nion.blackonionbot.oauth.OAuthHandler;
import com.github.black0nion.blackonionbot.oauth.api.SessionHandler;
import com.github.black0nion.blackonionbot.oauth.impl.DiscordAuthCodeToTokensImpl;
import com.github.black0nion.blackonionbot.rest.API;
import com.github.black0nion.blackonionbot.rest.sessions.AbstractSession;
import com.github.black0nion.blackonionbot.rest.sessions.MongoLogin;
import com.github.black0nion.blackonionbot.stats.*;
import com.github.black0nion.blackonionbot.systems.AutoRolesSystem;
import com.github.black0nion.blackonionbot.systems.JoinLeaveSystem;
import com.github.black0nion.blackonionbot.systems.ReactionRoleSystem;
import com.github.black0nion.blackonionbot.systems.docker.DockerManager;
import com.github.black0nion.blackonionbot.systems.giveaways.GiveawaySystem;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.systems.plugins.PluginSystem;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.ChainableArrayList;
import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.Paginator;
import com.github.ygimenez.model.PaginatorBuilder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Icon;
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
import org.slf4j.MDC;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.*;

public class Bot extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(Bot.class);

	public static final Icon BLACKONION_ICON;

	static {
		try {
			BLACKONION_ICON = Icon.from(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("logo.png")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static Bot instance;

	public static Bot getInstance() {
		return instance;
	}

	private JDA jda;

	public JDA getJDA() {
		return jda;
	}

	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
	public static final Gson GSON = new GsonBuilder()
		.registerTypeAdapterFactory(RecordTypeAdapterFactory.DEFAULT)
		.create();
	private final EventWaiter eventWaiter = new EventWaiter();
	private final HttpClient httpClient = HttpClient.newBuilder()
		.executor((Executors.newCachedThreadPool(
			new ThreadFactoryBuilder().setNameFormat("http-client-%d").build()
		)))
		.followRedirects(HttpClient.Redirect.ALWAYS)
		.build();

	private long selfUserId = -1;
	private final SlashCommandBase slashCommandBase;
	private final Config config;

	//region Getters
	public ExecutorService getExecutor() {
		return executor;
	}

	public ScheduledExecutorService getScheduledExecutor() {
		return scheduledExecutor;
	}


	public EventWaiter getEventWaiter() {
		return eventWaiter;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public long getSelfUserId() {
		return selfUserId;
	}

	public Config getConfig() {
		return config;
	}

	//endregion

	public Bot() throws IOException {
		instance = this;
		Utils.printLogo();
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

		ConfigFileLoader.loadConfig();
		config = new ConfigImpl(ConfigLoaderImpl.INSTANCE);
		// slf4j MDC; used to set the run mode in the logs sent to loki
		MDC.put("run_mode", config.getRunMode().name());

		DockerManager.init();
		logger.info("Starting BlackOnion-Bot in '{}' mode...", config.getRunMode());
		//noinspection ResultOfMethodCallIgnored
		new File("files").mkdirs();

		MongoManager.connect(config);

		InjectorMap injectorMap = new InjectorMap();
		SessionHandler sessionHandler = injectorMap.add(new MongoLogin());
		StatisticsManager statisticsManager = injectorMap.add(new StatisticsManager(config));
		AbstractSession.setSessionHandler(sessionHandler);
		injectorMap.add(new OAuthHandler(
			sessionHandler,
			injectorMap.add(new DiscordAuthCodeToTokensImpl(sessionHandler)))
		);

		Injector injector = new DefaultInjector(config, injectorMap);

		slashCommandBase = new SlashCommandBase(config, injector);
		injectorMap.add(slashCommandBase);

		final JDABuilder builder = JDABuilder.createDefault(config.getToken(), GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
			.disableCache(EnumSet.of(CacheFlag.CLIENT_STATUS, CacheFlag.ACTIVITY, CacheFlag.EMOJI, CacheFlag.STICKER))
			.enableCache(CacheFlag.VOICE_STATE)
			.setMemberCachePolicy(MemberCachePolicy.ALL)
			.enableIntents(GatewayIntent.GUILD_MEMBERS)
			.setMaxReconnectDelay(32)
			.addEventListeners(slashCommandBase, this, new ReactionRoleSystem(), new JoinLeaveSystem(config), new AutoRolesSystem(), statisticsManager, eventWaiter);

		LanguageSystem.init();
		// the constructor already needs the initialized hashmap
		ReloadCommand.initReloadableMethods();
		builder.setStatus(StatusCommand.getStatusFromConfig(config));
		builder.setActivity(ActivityCommand.getActivity(config));

		logger.info("Starting JDA...");
		try {
			this.jda = builder.build();
		} catch (final Exception e) {
			e.printStackTrace();
			logger.error("Failed to connect to the bot! Please make sure to provide the token correctly in either the environment variables or the .env file.");
			logger.error("Terminating bot.");
			System.exit(-1);
			return;
		}
		logger.info("JDA started successfully!");

		slashCommandBase.addCommands();

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

		PlayerManager playerManager = new PlayerManager(config);

		ChainableArrayList<Runnable> runnables = new ChainableArrayList<>();
		runnables
			.addAndGetSelf(GiveawaySystem::init)
			.addAndGetSelf(playerManager::init)
			.addAndGetSelf(() -> new API(config, injector, new StatsCollectorFactory(JettyCollector::initialize)))
			.addAndGetSelf(PluginSystem::loadPlugins)
			.addAndGetSelf(ConsoleCommands::run)
			.addAndGetSelf(() -> new Prometheus(config));

		ExecutorService asyncStartup = Executors.newFixedThreadPool(
			runnables.size(),
			new ThreadFactoryBuilder().setNameFormat("async-startup-%d").build()
		);

		runnables.forEach(r -> asyncStartup.submit(() -> {
			try {
				r.run();
			} catch (Exception e) {
				LoggerFactory.getLogger(r.getClass()).error("Error while starting up", e);
			}
		}));

		Runtime.getRuntime().addShutdownHook(new Thread(MongoManager::disconnect));

		statisticsManager.start();
	}

	@Override
	public void onReady(final ReadyEvent e) {
		final JDA jda = e.getJDA();
		selfUserId = jda.getSelfUser().getIdLong();
		logger.info("Connected to {}#{} in {}ms.", jda.getSelfUser().getName(), jda.getSelfUser().getDiscriminator(), (System.currentTimeMillis() - StatisticsManager.STARTUP_TIME));

		jda.getPresence().setActivity(ActivityCommand.getActivity(config));

		slashCommandBase.updateCommandsDev(jda);
	}

	@Reloadable("commands")
	private static void updateCommands() {
		instance.slashCommandBase.addCommands();
	}

	@Override
	public void onDisconnect(final DisconnectEvent event) {
		final CloseCode closeCode = event.getCloseCode();
		logger.error("Disconnected from Discord! Code: {}", (closeCode != null ? closeCode.name() + " = " + closeCode.getMeaning() : "NONE"));
	}

	@Override
	public void onReconnected(final @NotNull ReconnectedEvent event) {
		logger.info("Reconnected to Discord.");
	}
}
