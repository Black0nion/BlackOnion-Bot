package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.commands.common.AbstractCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;

import javax.annotation.Nullable;

public interface CommandRegistry {
	<T extends SlashCommand> T getCommand(Class<T> clazz);

	@Nullable
	@SuppressWarnings("rawtypes")
	AbstractCommand getCommand(String name);
}
