package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.api.Config;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;


public class SupportCommand extends SlashCommand {

	public SupportCommand(Config config) {
		super("support", "Used to get support.", config);
	}

	private final String botDevelopers = config.getMetadata().authors().entrySet().stream().map(entry -> "[" + entry.getKey() + "](" + entry.getValue() + ")").collect(Collectors.joining(", "));
	private final String blackonionAuthors = config.getMetadata().blackonion_authors().entrySet().stream().map(entry -> "[" + entry.getKey() + "](" + entry.getValue() + ")").collect(Collectors.joining(", "));

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		cmde.reply(cmde.success()
			.addField("gethelp", "[Discord Server](https://dsc.gg/blackonion)", false)
			.addField("botdevelopers", botDevelopers, false)
			.addField("blackoniondevs", blackonionAuthors, false));
	}
}
