/**
 *
 */
package com.github.black0nion.blackonionbot.systems;

import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbed;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Instant;


public class CustomCommand {

	private final BlackGuild guild;
	private String command;
	private TranslatedEmbed embed;
	private String answer;
	private boolean reply;

	public CustomCommand(final BlackGuild guild, final String command, final String answer) {
		this.guild = guild;
		this.command = command.toLowerCase();
		this.answer = answer;
	}

	@SuppressWarnings("unused")
	public CustomCommand(final BlackGuild guild, final String command, final TranslatedEmbed answer) {
		this.guild = guild;
		this.command = command.toLowerCase();
		this.embed = answer;
	}

	public CustomCommand(final BlackGuild guild) {
		this.guild = guild;
		// TODO: recreate
	}

	public CustomCommand setReply(final boolean reply) {
		this.reply = reply;
		return this;
	}

	/**
	 * @return the command
	 */
	public String getCommand() {
		return this.command;
	}

	/**
	 * @return the guild
	 */
	public BlackGuild getGuild() {
		return this.guild;
	}

	public void handle(final MessageReceivedEvent event) {
		if (this.embed != null) {
			this.embed.setTimestamp(Instant.now());
			final User author = event.getAuthor();
			this.embed.setFooter(author.getName() + "#" + author.getDiscriminator(), author.getEffectiveAvatarUrl());

			if (this.reply) {
				event.getMessage().replyEmbeds(this.embed.build()).queue();
			} else {
				event.getChannel().sendMessageEmbeds(this.embed.build()).queue();
			}
		} else if (this.answer != null) {
			if (this.reply) {
				event.getMessage().reply(this.answer).queue();
			} else {
				event.getChannel().sendMessage(this.answer).queue();
			}
		} else throw new NullPointerException("Both embed and Answer is null!");
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null || this.getClass() != obj.getClass()) return false;

		final CustomCommand cmd = (CustomCommand) obj;
		return cmd.getCommand().equalsIgnoreCase(this.getCommand()) && cmd.getGuild().getIdLong() == this.getGuild().getIdLong();
	}
}
