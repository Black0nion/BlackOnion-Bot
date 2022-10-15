package com.github.black0nion.blackonionbot.systems.customcommand;

import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Instant;

public class CustomCommandResponseEmbed implements CustomCommandResponse {

	private final TranslatedEmbed embed;

	public CustomCommandResponseEmbed(TranslatedEmbed embed) {
		this.embed = embed;
	}

	@Override
	public void handle(MessageReceivedEvent event, boolean reply) {
		this.embed.setTimestamp(Instant.now());
		final User author = event.getAuthor();
		this.embed.setFooter(author.getName() + "#" + author.getDiscriminator(), author.getEffectiveAvatarUrl());

		if (reply) event.getMessage().replyEmbeds(this.embed.build()).queue();
		else event.getChannel().sendMessageEmbeds(this.embed.build()).queue();
	}
}
