package com.github.black0nion.blackonionbot.commands.message;

import com.github.black0nion.blackonionbot.commands.common.AbstractCommand;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import javax.annotation.Nonnull;
import java.sql.SQLException;

public abstract class MessageCommand extends AbstractCommand<MessageCommandBuilder, CommandData> {

	protected MessageCommand(MessageCommandBuilder builder) {
		super(builder, null);
	}

	protected MessageCommand(MessageCommandBuilder builder, Config config) {
		super(builder, config);
	}

	public abstract void execute(final MessageCommandEvent cmde, final MessageContextInteractionEvent e, final Member member, final User author, final BlackGuild guild, final TextChannel channel, final Message message) throws SQLException;

	@Nonnull
	protected static MessageCommandBuilder builder(@Nonnull CommandData data) {
		return new MessageCommandBuilder(data);
	}

	@Nonnull
	protected static MessageCommandBuilder builder(@Nonnull String name) {
		return new MessageCommandBuilder(Commands.message(name));
	}
}
