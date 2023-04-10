package com.github.black0nion.blackonionbot.commands.slash;

import com.github.black0nion.blackonionbot.commands.common.AbstractCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.systems.language.Language;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SlashCommandEvent extends AbstractCommandEvent<SlashCommand, SlashCommandInteractionEvent> implements SlashCommandEventJdaUtils {

	private final TextChannel channel;

	public SlashCommandEvent(SlashCommandInteractionEvent e, Guild guild, Member member, User user, Language defaultLanguage, UserSettings userSettings, GuildSettings guildSettings) {
		this(null, e, guild, member, user, defaultLanguage, userSettings, guildSettings);
	}

	public SlashCommandEvent(SlashCommand cmd, SlashCommandInteractionEvent e, Guild guild, Member member, User user, Language defaultLanguage, UserSettings userSettings, GuildSettings guildSettings) {
		super(defaultLanguage, cmd, e, guild, member, user, userSettings, guildSettings);
		this.channel = e.getChannel().asTextChannel();
	}

	public TextChannel getChannel() {
		return channel;
	}
}
