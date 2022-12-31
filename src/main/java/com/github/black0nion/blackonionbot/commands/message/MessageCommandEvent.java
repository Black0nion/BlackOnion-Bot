package com.github.black0nion.blackonionbot.commands.message;

import com.github.black0nion.blackonionbot.commands.common.AbstractCommandEvent;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

import static java.util.Objects.requireNonNull;

public class MessageCommandEvent extends AbstractCommandEvent<MessageCommand, MessageContextInteractionEvent> {

	private final TextChannel channel;
	private final Message message;

	public MessageCommandEvent(MessageCommand cmd, MessageContextInteractionEvent event, BlackGuild guild, BlackMember member, BlackUser user, Language defaultLanguage) {
		super(defaultLanguage, cmd, event, guild, member, user);
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
