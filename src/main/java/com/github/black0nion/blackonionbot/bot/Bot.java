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
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.util.*;
import java.util.concurrent.*;

public class Bot extends ListenerAdapter {

	public static final Icon BLACKONION_ICON;

	static {
		try {
			BLACKONION_ICON = Icon.from(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("logo.png")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static final List<String> launchArguments = new ArrayList<>();

	private static Bot instance;

	public static Bot getInstance() {
		return instance;
	}

	private JDA jda;

	public JDA getJDA() {
		return jda;
	}

	private final Logger logger = LoggerFactory.getLogger(Bot.class);
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
	private final Gson gson = new GsonBuilder()
		.registerTypeAdapterFactory(RecordTypeAdapterFactory.DEFAULT)
		.create();
	private final EventWaiter eventWaiter = new EventWaiter();
	private final HttpClient httpClient = HttpClient.newBuilder()
		.executor(Executors.newCachedThreadPool(new ThreadFactory() {
			private final ThreadGroup group = new ThreadGroup("HttpClient");

			@Override
			public Thread newThread(@NotNull Runnable r) {
				return new Thread(group, r);
			}
		}))
		.followRedirects(HttpClient.Redirect.ALWAYS)
		.build();

	private long selfUserId = -1;

	//region Getters
	public ExecutorService getExecutor() {
		return executor;
	}

	public ScheduledExecutorService getScheduledExecutor() {
		return scheduledExecutor;
	}

	public Gson getGson() {
		return gson;
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
	//endregion

	public Bot(String[] args) throws IOException {
		instance = this;
		launchArguments.addAll(Arrays.asList(args));
		Utils.printLogo();
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		ConfigManager.loadConfig();
		DockerManager.init();
		logger.info("Starting BlackOnion-Bot in '{}' mode...", Config.getInstance().getRunMode());
		//noinspection ResultOfMethodCallIgnored
		new File("files").mkdirs();

		MongoManager.connect(Config.getInstance().getMongoConnectionString());

		final JDABuilder builder = JDABuilder.createDefault(Config.getInstance().getToken(), GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
			.disableCache(EnumSet.of(CacheFlag.CLIENT_STATUS, CacheFlag.ACTIVITY, CacheFlag.EMOJI, CacheFlag.STICKER))
			.enableCache(CacheFlag.VOICE_STATE)
			.setMemberCachePolicy(MemberCachePolicy.ALL)
			.enableIntents(GatewayIntent.GUILD_MEMBERS)
			.setMaxReconnectDelay(32)
			.addEventListeners(new SlashCommandBase(), this, new ReactionRoleSystem(), new JoinLeaveSystem(), new AutoRolesSystem(), new StatisticsManager(), eventWaiter);

		LanguageSystem.init();
		// the constructor already needs the initialized hashmap
		ReloadCommand.initReloadableMethods();
		SlashCommandBase.addCommands();
		builder.setStatus(StatusCommand.getStatusFromConfig());
		builder.setActivity(ActivityCommand.getActivity());

		try {
			this.jda = builder.build();
		} catch (final Exception e) {
			e.printStackTrace();
			logger.error("Failed to connect to the bot! Please make sure to provide the token correctly in either the environment variables or the .env file.");
			logger.error("Terminating bot.");
			System.exit(-1);
			return;
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
		runnables
			.addAndGetSelf(GiveawaySystem::init)
			.addAndGetSelf(PlayerManager::init)
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
		selfUserId = jda.getSelfUser().getIdLong();
		logger.info("Connected to {}#{} in {}ms.", jda.getSelfUser().getName(), jda.getSelfUser().getDiscriminator(), (System.currentTimeMillis() - StatisticsManager.STARTUP_TIME));

		jda.getPresence().setActivity(ActivityCommand.getActivity());

		SlashCommandBase.updateCommandsDev(jda);
	}

	@Reloadable("commands")
	public static void updateCommands() {
		SlashCommandBase.addCommands();
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
