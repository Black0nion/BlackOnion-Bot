package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.systems.reload.ReloadSystem;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleCommands extends Thread {

	private final ReloadSystem reloadSystem;

	private static final Logger logger = LoggerFactory.getLogger(ConsoleCommands.class);
	private final Bot bot;

	public ConsoleCommands(ReloadSystem reloadSystem, Bot bot) {
		this.reloadSystem = reloadSystem;
		this.bot = bot;
	}

	@Override
	public void run() {
		final Scanner sc = new Scanner(System.in);
		while (!this.isInterrupted()) {
			try {
				final String input = sc.nextLine();
				final String[] args = input.split(" ");
				if (input.equalsIgnoreCase("reload") || input.equalsIgnoreCase("rl")) {
					logger.info("Reloading...");
					reloadSystem.reloadAll();
					logger.info("Reloading done.");
				} else if (input.equalsIgnoreCase("guildlist")) {
					// parse boolean in args[1]
					boolean loadUser = args.length > 1 && Utils.isBoolean(args[1]) && Boolean.parseBoolean(args[1]);
					logger.info("Guilds: {}", bot.getJDA().getGuilds().stream().map(g -> {
						User user = null;
						if (loadUser) user = g.retrieveOwner().submit().join().getUser();
						return g.getName() + "(" + g.getId() + ")" + (user != null ? " | " + user.getAsTag() + "(" + user.getId() + ")" : "");
					}));
				} else if (input.equalsIgnoreCase("shutdown")) {
					Bot.getInstance().shutdown();
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
