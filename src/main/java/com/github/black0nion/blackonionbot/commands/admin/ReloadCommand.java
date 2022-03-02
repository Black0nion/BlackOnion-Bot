package com.github.black0nion.blackonionbot.commands.admin;

import com.github.black0nion.blackonionbot.Main;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.utils.NotImplementedException;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ReloadCommand extends SlashCommand {

	private static HashMap<String, Method> reloadableMethods = null;

	public ReloadCommand() {
		super(builder(
			Commands.slash("reload", "Reloads the bot")
				.addSubcommands(
					new SubcommandData("list", "Lists all reloadable methods"),
					new SubcommandData("method", "Reloads a specific method")
						.addOptions(new OptionData(OptionType.STRING, "method", "The method to reload", true)
							.addChoices(Utils.add(
								reloadableMethods.keySet().stream().map(m -> new Command.Choice(m, m)).toList(),
								new Command.Choice("ALL", "all"))
							)
						)
				)
		).setAdminGuild());
	}

	@Reloadable("reloadablemethods")
	public static void initReloadableMethods() {
		reloadableMethods = new HashMap<>();
		new Reflections(Main.class.getPackage().getName(), Scanners.MethodsAnnotated)
			.getMethodsAnnotatedWith(Reloadable.class)
			.forEach(method -> reloadableMethods.put(method.getAnnotation(Reloadable.class).value(), method));
	}

	public static void reload() {
		if (reloadableMethods == null) {
			initReloadableMethods();
		}
		reloadableMethods.values().forEach(m -> {
			try {
				m.invoke(m.getClass());
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		if (reloadableMethods == null) {
			initReloadableMethods();
		}

		if (e.getSubcommandName() == null) throw new NullPointerException("Subcommand name is null!");

		if (e.getSubcommandName().equals("list")) {

		} else {
			throw new NotImplementedException(e.getSubcommandName());
		}
		if (args.length >= 2) {
			if (args[1].equalsIgnoreCase("list") || args[1].equalsIgnoreCase("ls")) {
				String reloadableMethodsString = "```";
				for (final Map.Entry<String, Method> entry : reloadableMethods.entrySet()) {
					final Method meth = entry.getValue();
					reloadableMethodsString += "\n" + entry.getKey() + ": " + meth.getDeclaringClass().getSimpleName() + "." + meth.getName();
				}
				channel.sendMessageEmbeds(cmde.success().addField("reloadables", reloadableMethodsString + "```", false).build()).delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
				return;
			}
			boolean invalidConfigs = false;
			for (final String argument : Utils.removeFirstArg(args))
				if (reloadableMethods.containsKey(argument)) {
					final Method method = reloadableMethods.get(argument);
					try {
						method.invoke(method.getClass());
						channel.sendMessageEmbeds(cmde.success().addField(cmde.getTranslation("configreload", new Placeholder("config", argument.toUpperCase())), "messagedelete5", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
					} catch (final Exception ex) {
						cmde.exception();
					}
				} else {
					invalidConfigs = true;
				}
			if (invalidConfigs) {
				final String translation = cmde.getTranslation("availableconfigs", new Placeholder("configs", reloadableMethods.toString()));
				System.out.println(translation);
				channel.sendMessageEmbeds(cmde.error().addField("invalidconfig", translation, false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
			}
		} else {
			channel.sendMessageEmbeds(cmde.success().addField("reloading", "messagedelete5", false).build()).queue(msg -> {
				final ScheduledFuture<?> task = Bot.scheduledExecutor.schedule(() -> {
					msg.editMessageEmbeds(cmde.error().addField("i fucked up", "lol reload command broken XD go fix it dumbass", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
				}, 5, TimeUnit.SECONDS);

				reload();
				task.cancel(true);

				final MessageEmbed builder = cmde.success().addField("configsreload", "messagedelete5", false).build();
				if (msg == null) {
					channel.sendMessageEmbeds(builder).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
				} else {
					msg.editMessageEmbeds(builder).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
				}
			});
		}
	}
}