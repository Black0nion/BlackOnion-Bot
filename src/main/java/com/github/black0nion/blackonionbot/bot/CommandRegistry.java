package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.commands.common.Command;

import javax.annotation.Nullable;

public interface CommandRegistry {
	<T extends Command> T getCommand(Class<T> clazz);

	@Nullable
	Command getCommand(String name);
}