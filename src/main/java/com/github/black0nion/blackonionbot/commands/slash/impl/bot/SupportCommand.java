package com.github.black0nion.blackonionbot.commands.slash.impl.bot;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.config.immutable.ConfigFileLoader;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;


public class SupportCommand extends SlashCommand {

	public SupportCommand() {
		super("support", "Used to get support.");
	}

	private static final String BOT_DEVELOPERS = ConfigFileLoader.getMetadata()
		// username:link
		.authors()
		.entrySet()
		.stream()
		.map(entry -> "[" + entry.getKey() + "](" + entry.getValue() + ")")
		.collect(Collectors.joining(", "));

	private static final String BLACKONION_AUTHORS = ConfigFileLoader.getMetadata()
		// username:link
		.blackonion_authors()
		.entrySet()
		.stream()
		// create markdown links
		.map(entry -> "[" + entry.getKey() + "](" + entry.getValue() + ")")
		.collect(Collectors.joining(", "));

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, SlashCommandInteractionEvent e, Member member, User author, Guild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		cmde.reply(cmde.success()
			.addField("gethelp", "[Discord Server](https://dsc.gg/blackonion)", false)
			.addField("botdevelopers", BOT_DEVELOPERS, false)
			.addField("blackoniondevs", BLACKONION_AUTHORS, false));
	}
}
