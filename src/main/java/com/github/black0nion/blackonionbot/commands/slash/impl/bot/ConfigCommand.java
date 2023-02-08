package com.github.black0nion.blackonionbot.commands.slash.impl.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class ConfigCommand extends SlashCommand {

	public ConfigCommand() {
		super(builder(Commands.slash("config", "Configure various settings.")
			.addOption(OptionType.STRING, "setting", "The setting to configure.", true)
			.addOption(OptionType.STRING, "value", "The value to set the setting to.", true)
		));
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		final ObjectMapper mapper = new ObjectMapper();
		logger.debug("User {}'s settings BEFORE: {}", author.getDebugMessage(), userSettings);
		logger.debug("Guild {}'s settings BEFORE: {}", guild.getDebugMessage(), guildSettings);
		userSettings.getSetting(cmde.getOption("setting", OptionMapping::getAsString)).setParsedValue(cmde.getOption("value", OptionMapping::getAsString));
		logger.debug("User {}'s settings AFTER: {}", author.getDebugMessage(), userSettings);
		logger.debug("Guild {}'s settings AFTER: {}", guild.getDebugMessage(), guildSettings);
		String message = "```json\n" + mapper.writeValueAsString(userSettings) + "\n```\n```json\n" + mapper.writeValueAsString(guildSettings) + "\n```";
		cmde.send(message);
	}
}
