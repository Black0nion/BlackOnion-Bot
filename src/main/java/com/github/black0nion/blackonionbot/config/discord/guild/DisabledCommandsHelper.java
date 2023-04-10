package com.github.black0nion.blackonionbot.config.discord.guild;

import com.github.black0nion.blackonionbot.commands.common.Command;
import com.github.black0nion.blackonionbot.config.discord.impl.settings.ListSetting;

import java.util.Set;

public interface DisabledCommandsHelper {
	ListSetting<Command, Set<Command>> getDisabledCommands();

	default boolean isCommandActivated(Command command) {
		return !isCommandDisabled(command);
	}

	default boolean isCommandDisabled(Command command) {
		return getDisabledCommands().contains(command);
	}

	default boolean disableCommand(Command command) {
		return getDisabledCommands().add(command);
	}

	default boolean enableCommand(Command command) {
		return getDisabledCommands().remove(command);
	}


	default boolean setCommandActivated(Command command, boolean activated) {
		if (activated) {
			return enableCommand(command);
		} else {
			return disableCommand(command);
		}
	}

	default boolean toggleCommand(Command command) {
		if (isCommandDisabled(command)) {
			return enableCommand(command);
		} else {
			return disableCommand(command);
		}
	}
}