package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.commands.slash.impl.admin.ActivityCommand;
import com.github.black0nion.blackonionbot.commands.slash.impl.admin.StatusCommand;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettingsRepo;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettingsRepo;
import com.github.black0nion.blackonionbot.config.featureflags.FeatureFlags;
import com.github.black0nion.blackonionbot.config.featureflags.impl.FeatureFlagFactoryImpl;
import com.github.black0nion.blackonionbot.config.immutable.ConfigFileLoader;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.config.immutable.impl.ConfigImpl;
import com.github.black0nion.blackonionbot.config.immutable.impl.ConfigLoaderImpl;
import com.github.black0nion.blackonionbot.config.mutable.api.Settings;
import com.github.black0nion.blackonionbot.config.mutable.impl.MutableConfigLoaderImpl;
import com.github.black0nion.blackonionbot.config.mutable.impl.SettingsImpl;
import com.github.black0nion.blackonionbot.database.DatabaseConnector;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.inject.DefaultInjector;
import com.github.black0nion.blackonionbot.inject.Injector;
import com.github.black0nion.blackonionbot.inject.InjectorMap;
import com.github.black0nion.blackonionbot.oauth.OAuthHandler;
import com.github.black0nion.blackonionbot.oauth.api.SessionHandler;
import com.github.black0nion.blackonionbot.oauth.impl.DiscordAuthCodeToTokensImpl;
import com.github.black0nion.blackonionbot.rest.API;
import com.github.black0nion.blackonionbot.rest.sessions.AbstractSession;
import com.github.black0nion.blackonionbot.rest.sessions.DatabaseSessionHandler;
import com.github.black0nion.blackonionbot.stats.JettyCollector;
import com.github.black0nion.blackonionbot.stats.Prometheus;
import com.github.black0nion.blackonionbot.stats.StatisticsManager;
import com.github.black0nion.blackonionbot.stats.StatsCollectorFactory;
import com.github.black0nion.blackonionbot.systems.AutoRolesSystem;
import com.github.black0nion.blackonionbot.systems.JoinLeaveSystem;
import com.github.black0nion.blackonionbot.systems.ReactionRoleSystem;
import com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerSystem;
import com.github.black0nion.blackonionbot.systems.giveaways.GiveawaySystem;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystemImpl;
import com.github.black0nion.blackonionbot.systems.plugins.PluginSystem;
import com.github.black0nion.blackonionbot.systems.reload.ReloadSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.ChainableArrayList;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
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
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.events.session.SessionRecreateEvent;
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
import java.net.http.HttpClient;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Bot extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(Bot.class);

	public static final Icon BLACKONION_ICON;

	static {
		try {
			BLACKONION_ICON = Icon.from(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("logo.png")));
		} catch (IOException e) {
			throw new RuntimeException(e); // NOSONAR
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
	private final Settings settings;
	private final DatabaseConnector database;
	private final SQLHelperFactory sqlHelperFactory;
	private final GiveawaySystem giveawaySystem;
	private final PluginSystem pluginSystem;

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

	/**
	 * Don't use in new code, prefer dependency injection to allow unit testing
	 */
	public SQLHelperFactory getSqlHelperFactory() {
		return sqlHelperFactory;
	}

	//endregion

	public Bot() throws Exception { // NOSONAR
		instance = this; // NOSONAR
		final long startTime = System.currentTimeMillis();
		Utils.printLogo();
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

		Thread shutdownHookThread = new Thread(this::shutdown);
		shutdownHookThread.setName("ShutdownHook");
		Runtime.getRuntime().addShutdownHook(shutdownHookThread);

		FeatureFlags featureFlags = new FeatureFlags(new FeatureFlagFactoryImpl());
		ConfigFileLoader.loadConfig();
		config = new ConfigImpl(ConfigLoaderImpl.INSTANCE);
		settings = new SettingsImpl(MutableConfigLoaderImpl.INSTANCE);

		logger.info("Starting BlackOnion-Bot in '{}' mode...", config.getRunMode());
		//noinspection ResultOfMethodCallIgnored
		new File("files").mkdirs();

		ReloadSystem reloadSystem = new ReloadSystem();

		InjectorMap injectorMap = new InjectorMap();
		injectorMap.add(reloadSystem);
		injectorMap.add(this);
		injectorMap.add(featureFlags);
		injectorMap.add(config);
		injectorMap.add(settings);

		injectorMap.add(database = new DatabaseConnector(config, featureFlags)); // NOSONAR
		sqlHelperFactory = database.getSqlHelperFactory();
		injectorMap.add(sqlHelperFactory);

		SessionHandler sessionHandler = injectorMap.add(new DatabaseSessionHandler(sqlHelperFactory));
		StatisticsManager statisticsManager = injectorMap.add(new StatisticsManager(config, featureFlags));

		AbstractSession.setSessionHandler(sessionHandler);
		injectorMap.add(new OAuthHandler(
			sessionHandler,
			injectorMap.add(new DiscordAuthCodeToTokensImpl(sessionHandler)))
		);

		Injector injector = new DefaultInjector(config, injectorMap);

		LanguageSystem languageSystem = new LanguageSystemImpl(reloadSystem);
		injectorMap.add(languageSystem);

		EmbedUtils embedUtils = new EmbedUtils(languageSystem);
		injectorMap.add(embedUtils);

		slashCommandBase = new SlashCommandBase(config, injector, reloadSystem);
		injectorMap.add(SlashCommandBase.class, slashCommandBase);
		injectorMap.add(CommandRegistry.class, slashCommandBase);

		giveawaySystem = new GiveawaySystem(sqlHelperFactory, languageSystem);
		injectorMap.add(giveawaySystem);

		new BlackGuild.GuildCacheClear(reloadSystem);
		new BlackUser.UserCacheClear(reloadSystem);
		new BlackMember.MemberCacheClear(reloadSystem);

		this.pluginSystem = new PluginSystem(this);
		reloadSystem.registerReloadable(pluginSystem);

		final JDABuilder builder = JDABuilder.createDefault(config.getToken(), GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
			.disableCache(EnumSet.of(CacheFlag.CLIENT_STATUS, CacheFlag.ACTIVITY, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS))
			.enableCache(CacheFlag.VOICE_STATE)
			.setMemberCachePolicy(MemberCachePolicy.ALL)
			.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
			.setMaxReconnectDelay(32)
			.addEventListeners(
				slashCommandBase,
				this,
				new ReactionRoleSystem(sqlHelperFactory),
				new JoinLeaveSystem(config, settings, languageSystem, embedUtils),
				new AutoRolesSystem(languageSystem),
				new AntiSpoilerSystem(languageSystem, embedUtils),
				statisticsManager,
				eventWaiter
			);

		// the constructor already needs the initialized hashmap
		builder.setStatus(StatusCommand.getStatusFromConfig(settings));
		builder.setActivity(ActivityCommand.getActivity(settings));

		if (featureFlags.bot_shutdownBeforeConnection.getValue()) {
			logger.warn("Shutting down before connecting to Discord due to the 'bot.shutdownBeforeConnection' feature flag");
			System.exit(0);
		}

		logger.info("Starting JDA...");
		try {
			this.jda = builder.build();
		} catch (final Exception e) {
			logger.error("Failed to connect to the bot! Please make sure to provide the token correctly in either the environment variables or the .env file.", e);
			logger.error("Terminating bot.");
			System.exit(-1);
			return;
		}
		logger.info("JDA started successfully!");

		UserSettingsRepo userSettingsRepo = injectorMap.add(new UserSettingsRepo(injector.getInstance(SQLHelperFactory.class), l -> jda.retrieveUserById(l), languageSystem));
		GuildSettingsRepo guildSettingsRepo = injectorMap.add(new GuildSettingsRepo(injector.getInstance(SQLHelperFactory.class), l -> jda.getGuildById(l), languageSystem, slashCommandBase));
		slashCommandBase.setUserSettingsRepo(userSettingsRepo);
		slashCommandBase.setGuildSettingsRepo(guildSettingsRepo);

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
			throw e instanceof RuntimeException ex ? ex : new RuntimeException(e);
		}

		ChainableArrayList<Runnable> runnables = new ChainableArrayList<>();
		runnables
			.addAndGetSelf(() -> reloadSystem.registerReloadable(new API(config, injector, new StatsCollectorFactory(JettyCollector::initialize))))
			.addAndGetSelf(() -> pluginSystem.loadPlugins(null))
			.addAndGetSelf(() -> reloadSystem.registerReloadable(new Prometheus(config)));

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
		new ConsoleCommands(reloadSystem, this).start();

		statisticsManager.start();

		logger.info("Successfully started the application tasks in {} ms!", System.currentTimeMillis() - startTime);
	}

	public void shutdown() {
		logger.info("Shutting down...");
		pluginSystem.disablePlugins();
		// shutdown executors
		executor.shutdown();
		scheduledExecutor.shutdown();
		// shutdown jda
		if (jda != null) {
			jda.shutdown();
		}
		// shutdown HikariCP
		if (database != null) {
			database.close();
		}
	}

	@Override
	public void onReady(final ReadyEvent e) {
		final JDA readyJda = e.getJDA();
		selfUserId = readyJda.getSelfUser().getIdLong();
		logger.info("Connected to {}#{} in {}ms.", readyJda.getSelfUser().getName(), readyJda.getSelfUser().getDiscriminator(), (System.currentTimeMillis() - StatisticsManager.STARTUP_TIME));

		slashCommandBase.updateCommandsDev(readyJda);
		executor.submit(giveawaySystem::init);
	}

	@Override
	public void onSessionDisconnect(final SessionDisconnectEvent event) {
		final CloseCode closeCode = event.getCloseCode();
		logger.error("Disconnected from Discord! Code: {}", (closeCode != null ? closeCode.name() + " = " + closeCode.getMeaning() : "NONE"));
	}

	@Override
	public void onSessionRecreate(final @NotNull SessionRecreateEvent event) {
		logger.info("Reconnected to Discord.");
	}
}
