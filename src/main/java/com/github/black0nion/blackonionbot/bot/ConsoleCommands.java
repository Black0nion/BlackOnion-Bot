package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.commands.admin.ReloadCommand;
import com.github.black0nion.blackonionbot.misc.LogMode;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.systems.plugins.PluginSystem;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.entities.User;
import spark.Spark;

import javax.annotation.Nonnull;
import java.util.*;

import static com.github.black0nion.blackonionbot.bot.Bot.out;

public class ConsoleCommands {

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

	static {
		logLevel.addAll(Arrays.asList(LogMode.values()));
		logOrigin.addAll(Arrays.asList(LogOrigin.values()));
	}

	public static void run() {
		final Scanner sc = new Scanner(System.in);
		while (true) {
			try {
				final String input = sc.nextLine();
				final String[] args = input.split(" ");
				if (input.startsWith("peek")) {
					if (args.length < 2) {
						out.println("Invalid Syntax. Syntax: peek <category> [limit]| Valid Categories: " + String.join(", ", LogOrigin.getNames()) + ", valid LogModes: " + String.join(", ", LogMode.getNames()));
					} else if (Utils.equalsOneIgnoreCase(args[1], LogOrigin.getNames())) {
						if (args.length >= 3) {
							if (!Utils.isLong(args[2])) {
								out.println("Invalid number!");
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
				} else if (input.equalsIgnoreCase("guildlist")) {
					// parse boolean in args[1]
					boolean loadUser = args.length > 1 && Utils.isBoolean(args[1]) && Boolean.parseBoolean(args[1]);
					out.println("Guilds: " + Bot.jda.getGuilds().stream().map(g -> {
						User user = null;
						if (loadUser) user = g.retrieveOwner().submit().join().getUser();
						return g.getName() + "(" + g.getId() + ")" + (user != null ? " | " + user.getAsTag() + "(" + user.getId() + ")" : "");
					}));
				} else if (input.equalsIgnoreCase("shutdown")) {
					Logger.logWarning("Shutting down...", LogOrigin.BOT);
					Bot.jda.shutdown();
					Spark.stop();
					PluginSystem.disablePlugins();
					Spark.awaitStop();
					Logger.logWarning("Successfully disconnected!", LogOrigin.BOT);
					System.exit(0);
				} else {
					out.println("Command not recognized. Valid Commands: [reload, shutdown, setlogorigin, setloglevel, peek]");
				}
			} catch (final Exception e) {
				// docker doesn't have stdin
				if (e instanceof NoSuchElementException) return;
				e.printStackTrace();
			}
		}
	}
}