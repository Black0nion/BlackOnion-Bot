package com.github.black0nion.blackonionbot.config.discord.guild;

import com.github.black0nion.blackonionbot.commands.common.NamedCommand;
import com.github.black0nion.blackonionbot.config.discord.impl.settings.ListSetting;

import java.util.Set;

public interface DisabledCommandsHelper {
	ListSetting<NamedCommand, Set<NamedCommand>> getDisabledCommands();

	default boolean isCommandActivated(NamedCommand command) {
		return !isCommandDisabled(command);
	}

	default boolean isCommandDisabled(NamedCommand command) {
		return getDisabledCommands().contains(command);
	}

	default boolean disableCommand(NamedCommand command) {
		return getDisabledCommands().add(command);
	}

	default boolean enableCommand(NamedCommand command) {
		return getDisabledCommands().remove(command);
	}


	default boolean setCommandActivated(NamedCommand command, boolean activated) {
		if (activated) {
			return enableCommand(command);
		} else {
			return disableCommand(command);
		}
	}

	default boolean toggleCommand(NamedCommand command) {
		if (isCommandDisabled(command)) {
			return enableCommand(command);
		} else {
			return disableCommand(command);
		}
	}
}
