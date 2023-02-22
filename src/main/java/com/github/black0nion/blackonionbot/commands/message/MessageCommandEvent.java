package com.github.black0nion.blackonionbot.commands.message;

import com.github.black0nion.blackonionbot.commands.common.AbstractCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.systems.language.Language;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

import static java.util.Objects.requireNonNull;

public class MessageCommandEvent extends AbstractCommandEvent<MessageCommand, MessageContextInteractionEvent> {

	private final TextChannel channel;
	private final Message message;

	public MessageCommandEvent(MessageCommand cmd, MessageContextInteractionEvent event, Guild guild, Member member, User user, Language defaultLanguage, UserSettings userSettings, GuildSettings guildSettings) {
		super(defaultLanguage, cmd, event, guild, member, user, userSettings, guildSettings);
		// never null according to the docs
		this.channel = requireNonNull(event.getChannel()).asTextChannel();
		this.message = event.getTarget();
	}

	public TextChannel getChannel() {
		return channel;
	}

	public Message getMessage() {
		return message;
	}
}
