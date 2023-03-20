package com.github.black0nion.blackonionbot.systems.customcommand;

import net.dv8tion.jda.api.entities.Guild;

import java.util.Map;

public interface CustomCommandRepository {
	default Map<String, CustomCommand> getCustomCommands(Guild guild) {
		return getCustomCommands(guild.getIdLong());
	}

	Map<String, CustomCommand> getCustomCommands(long guildId);
}
