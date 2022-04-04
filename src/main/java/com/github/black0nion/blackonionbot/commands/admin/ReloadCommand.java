package com.github.black0nion.blackonionbot.commands.admin;

import com.github.black0nion.blackonionbot.Main;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.utils.NotImplementedException;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
								// don't replace with toList because the result would be immutable
								reloadableMethods.keySet().stream().map(m -> new Command.Choice(m, m)).collect(Collectors.toList()),
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
		e.deferReply().queue();
		if (reloadableMethods == null) {
			initReloadableMethods();
		}

		if (e.getSubcommandName() == null) throw new NullPointerException("Subcommand name is null!");

		if (e.getSubcommandName().equals("list")) {
			StringBuilder reloadableMethodsString = new StringBuilder("```");
			for (final Map.Entry<String, Method> entry : reloadableMethods.entrySet()) {
				final Method meth = entry.getValue();
				reloadableMethodsString
					.append("\n")
					.append(entry.getKey())
					.append(": ")
					.append(meth.getDeclaringClass().getSimpleName())
					.append(".")
					.append(meth.getName());
			}
			cmde.success("reloadables", reloadableMethodsString.append("```").toString());
		} else if (e.getSubcommandName().equals("method")) {
			String option = e.getOption("method", "all", OptionMapping::getAsString);
			if (option.equalsIgnoreCase("all")) {
				reload();
				cmde.send("configsreload");
			} else {
				final @Nullable Method method = reloadableMethods.get(option);
				if (method == null) throw new NullPointerException("Invalid method.");
				try {
					method.invoke(method.getClass());
					cmde.send("configreload", new Placeholder("config", option));
				} catch (IllegalAccessException | InvocationTargetException ex) {
					cmde.exception(ex);
				}
			}
		} else {
			throw new NotImplementedException(e.getSubcommandName());
		}
	}
}