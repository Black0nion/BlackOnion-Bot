package com.github.black0nion.blackonionbot.commands.slash;

import com.github.black0nion.blackonionbot.commands.common.AbstractCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SlashCommandEvent extends AbstractCommandEvent<SlashCommand, SlashCommandInteractionEvent> implements SlashCommandEventJdaUtils {

	private final TextChannel channel;

	public SlashCommandEvent(SlashCommandInteractionEvent e, BlackGuild guild, BlackMember member, BlackUser user, Language defaultLanguage, UserSettings userSettings) {
		this(null, e, guild, member, user, defaultLanguage, userSettings);
	}

	public SlashCommandEvent(SlashCommand cmd, SlashCommandInteractionEvent e, BlackGuild guild, BlackMember member, BlackUser user, Language defaultLanguage, UserSettings userSettings) {
		super(defaultLanguage, cmd, e, guild, member, user, userSettings);
		this.channel = e.getChannel().asTextChannel();
	}

	public TextChannel getChannel() {
		return channel;
	}
}
