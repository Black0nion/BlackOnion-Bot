package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.api.API;
import com.github.black0nion.blackonionbot.commands.admin.ReloadCommand;
import com.github.black0nion.blackonionbot.systems.plugins.PluginSystem;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleCommands {

	private static final Logger logger = LoggerFactory.getLogger(ConsoleCommands.class);

	public static void run() {
		final Scanner sc = new Scanner(System.in);
		while (true) {
			try {
				final String input = sc.nextLine();
				final String[] args = input.split(" ");
				if (input.equalsIgnoreCase("reload") || input.equalsIgnoreCase("rl")) {
					logger.info("Reloading...");
					ReloadCommand.reload();
					logger.info("Reloading done.");
				} else if (input.equalsIgnoreCase("guildlist")) {
					// parse boolean in args[1]
					boolean loadUser = args.length > 1 && Utils.isBoolean(args[1]) && Boolean.parseBoolean(args[1]);
					logger.info("Guilds: " + Bot.getInstance().getJda().getGuilds().stream().map(g -> {
						User user = null;
						if (loadUser) user = g.retrieveOwner().submit().join().getUser();
						return g.getName() + "(" + g.getId() + ")" + (user != null ? " | " + user.getAsTag() + "(" + user.getId() + ")" : "");
					}));
				} else if (input.equalsIgnoreCase("shutdown")) {
					logger.warn("Shutting down...");
					Bot.getInstance().getJda().shutdown();
					PluginSystem.disablePlugins();
					API.getApp().close();
					logger.warn("Successfully disconnected!");
					System.exit(0);
				} else {
					logger.info("Command not recognized. Valid Commands: [reload, shutdown, setlogorigin, setloglevel, peek]");
				}
			} catch (final Exception e) {
				// docker doesn't have stdin
				if (e instanceof NoSuchElementException) return;
				e.printStackTrace();
			}
		}
	}
}