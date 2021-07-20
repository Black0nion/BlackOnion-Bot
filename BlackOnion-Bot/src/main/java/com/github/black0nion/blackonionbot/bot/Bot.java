package com.github.black0nion.blackonionbot.bot;

import java.io.File;
import java.io.PrintStream;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.Nonnull;

import com.github.black0nion.blackonionbot.API.API;
import com.github.black0nion.blackonionbot.commands.admin.ActivityCommand;
import com.github.black0nion.blackonionbot.commands.admin.ReloadCommand;
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
import com.github.black0nion.blackonionbot.utils.CatchLogs;
import com.github.black0nion.blackonionbot.utils.CredentialsManager;
import com.github.black0nion.blackonionbot.utils.JarUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.ValueManager;
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
import spark.Spark;

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

    /**
     * Empty for everything
     */
    @Nonnull
    public static final List<LogMode> logLevel = new ArrayList<>();
    /**
     * Empty for everything
     */
    @Nonnull
    public static final List<LogOrigin> logOrigin = new ArrayList<>();

    public static final PrintStream out = System.out;
    public static final PrintStream err = System.err;

    @SuppressWarnings("resource")
    public void startBot() {
	Utils.printLogo();
	CatchLogs.init();
	new ValueManager();
	DefaultValues.init();
	isJarFile = JarUtils.runningFromJar();
	logLevel.addAll(Arrays.asList(LogMode.values()));
	logOrigin.addAll(Arrays.asList(LogOrigin.values()));
	Logger.log(LogMode.INFORMATION, "Starting BlackOnion-Bot in " + runMode + " mode...");
	new File("files").mkdir();

	credentialsManager = new CredentialsManager(runMode.name().toLowerCase());

	if (credentialsManager.has("mongo_connection_string")) {
	    MongoManager.connect(credentialsManager.getString("mongo_connection_string"));
	} else {
	    MongoManager.connect(credentialsManager.getString("mongo_ip"), credentialsManager.getString("mongo_port"), credentialsManager.getString("mongo_authdb"), credentialsManager.getString("mongo_username"), credentialsManager.getString("mongo_password"), credentialsManager.getInt("mongo_timeout"));
	}

	final JDABuilder builder = JDABuilder.createDefault(credentialsManager.getString("token"), GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_REACTIONS).disableCache(EnumSet.of(CacheFlag.CLIENT_STATUS, CacheFlag.ACTIVITY, CacheFlag.EMOTE)).enableCache(CacheFlag.VOICE_STATE).setMemberCachePolicy(MemberCachePolicy.ALL).enableIntents(GatewayIntent.GUILD_MEMBERS);

	final EventWaiter waiter = new EventWaiter();

	builder.addEventListeners(new CommandBase(), new SlashCommandBase(), new Bot(), new ReactionRoleSystem(), new HandRaiseSystem(), new JoinLeaveSystem(), new AutoRolesSystem(), new EventEndpoint(), waiter);

	LanguageSystem.init();
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

	CommandBase.addCommands(waiter);
	SlashCommandBase.addCommands(waiter);
	InfluxManager.init();
	BotInformation.init();
	// BirthdaySystem.init();
	GiveawaySystem.init();
	PlayerManager.init();
	// MusicSystem.init();

	executor.submit(() -> {
	    Newssystem.init();
	});

	executor.submit(() -> {
	    API.init();
	});

	executor.submit(() -> {
	    PluginSystem.loadPlugins();
	});

	executor.submit(() -> {
	    final Scanner sc = new Scanner(System.in);
	    while (true) {
		try {
		    final String input = sc.nextLine();
		    final String[] args = input.split(" ");
		    if (input.startsWith("peek")) {
			if (args.length < 2) {
			    out.println("Invalid Syntax. Syntax: peek <category> [limit]| Valid Categories: " + String.join(", ", LogOrigin.getNames()) + ", valid LogModes: " + String.join(", ", LogMode.getNames()));
			    continue;
			} else if (Utils.equalsOneIgnoreCase(args[1], LogOrigin.getNames())) {
			    if (args.length >= 3) {
				if (!Utils.isLong(args[2])) {
				    out.println("Invalid number!");
				    continue;
				} else {
				    Logger.printForCategory(LogOrigin.valueOf(args[1].toUpperCase()), Integer.parseInt(args[2]));
				}
			    } else {
				Logger.printForCategory(LogOrigin.valueOf(args[1].toUpperCase()));
			    }
			} else if (Utils.equalsOneIgnoreCase(args[1], LogMode.getNames())) {
			    if (args.length >= 3) {
				if (!Utils.isLong(args[2])) {
				    out.println("Invalid number!");
				    continue;
				} else {
				    Logger.printForLevel(LogMode.valueOf(args[1].toUpperCase()), Integer.parseInt(args[2]));
				}
			    } else {
				Logger.printForLevel(LogMode.valueOf(args[1].toUpperCase()));
			    }
			} else if (args[1].equalsIgnoreCase("all")) {
			    if (args.length >= 3) {
				if (!Utils.isLong(args[2])) {
				    out.println("Invalid number!");
				    continue;
				} else {
				    Logger.printAll(Integer.parseInt(args[2]));
				}
			    } else {
				Logger.printAll();
			    }
			} else {
			    out.println("Category not found. Valid Categories: " + String.join(", ", LogOrigin.getNames()) + ", valid LogModes: " + String.join(", ", LogMode.getNames()));
			}
		    } else if (input.startsWith("setloglevel")) {
			if (args.length <= 1) {
			    out.println("Please use setloglevel <level> | Valid levels: " + String.join(", ", LogMode.getNames()));
			} else {
			    for (final String cat : Utils.removeFirstArg(args)) {
				if (cat.startsWith("!")) {
				    final LogMode parsed = LogMode.parse(cat.replace("!", ""));
				    if (parsed != null) {
					logLevel.remove(parsed);
				    } else {
					out.println(cat + " is not a valid LogMode!");
				    }
				} else {
				    final LogMode parsed = LogMode.parse(cat);
				    if (parsed != null) {
					if (!logLevel.contains(parsed)) {
					    logLevel.add(parsed);
					}
				    } else {
					out.println(cat + " is not a valid LogMode!");
				    }
				}
			    }
			    out.println("Now printing LogLevels " + logLevel);
			}
		    } else if (input.startsWith("setlogorigin")) {
			if (args.length <= 1) {
			    out.println("Please use setlogorigin <origin> | Valid origins: " + String.join(", ", LogOrigin.getNames()));
			} else {
			    for (final String cat : Utils.removeFirstArg(args)) {
				if (cat.startsWith("!")) {
				    final LogOrigin parsed = LogOrigin.parse(cat.replace("!", ""));
				    if (parsed != null) {
					logOrigin.remove(parsed);
				    } else {
					out.println(cat + " is not a valid LogOrigin!");
				    }
				} else {
				    final LogOrigin parsed = LogOrigin.parse(cat);
				    if (parsed != null) {
					if (!logOrigin.contains(parsed)) {
					    logOrigin.add(parsed);
					}
				    } else {
					out.println(cat + " is not a valid LogOrigin!");
				    }
				}
			    }
			    out.println("Now printing LogOrigin " + logOrigin);
			}
		    } else if (input.equalsIgnoreCase("reload") || input.equalsIgnoreCase("rl")) {
			Logger.logInfo("Reloading...", LogOrigin.BOT);
			ReloadCommand.reload();
			Logger.logInfo("Reloading done.", LogOrigin.BOT);
		    } else if (input.equalsIgnoreCase("shutdown")) {
			Logger.logWarning("Shutting down...", LogOrigin.BOT);
			jda.shutdown();
			Spark.stop();
			PluginSystem.disablePlugins();
			Spark.awaitStop();
			Logger.logWarning("Successfully disconnected!", LogOrigin.BOT);
			System.exit(0);
		    } else {
			out.println("Command not recognized. Valid Commands: [reload, shutdown, setlogorigin, setloglevel, peek]");
		    }
		} catch (final Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    @Override
    public void onReady(final ReadyEvent e) {
	BotInformation.botId = e.getJDA().getSelfUser().getIdLong();
	Logger.log(LogMode.INFORMATION, LogOrigin.BOT, "Connected to " + e.getJDA().getSelfUser().getName() + "#" + e.getJDA().getSelfUser().getDiscriminator() + " in " + (System.currentTimeMillis() - Bot.startTime) + "ms.");

	switchingStatusCallable = () -> {
	    while (true) {
		try {
		    final String activityType = ValueManager.getString("activityType");
		    if (activityType != null && (!activityType.equalsIgnoreCase("") && !(activityType.equalsIgnoreCase("none")))) {
			jda.getPresence().setActivity(ActivityCommand.getActivity());
			Thread.sleep(60000);
		    }
		    jda.getPresence().setActivity(Activity.listening(BotInformation.line_count + " lines of code in " + BotInformation.file_count + " files"));
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