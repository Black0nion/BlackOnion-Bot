package com.github.black0nion.blackonionbot.commands.message;

import com.github.black0nion.blackonionbot.commands.common.AbstractCommandBuilder;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class MessageCommandBuilder extends AbstractCommandBuilder<MessageCommandBuilder, CommandData> {

	public MessageCommandBuilder(final CommandData data) {
		super(data);
	}
}
