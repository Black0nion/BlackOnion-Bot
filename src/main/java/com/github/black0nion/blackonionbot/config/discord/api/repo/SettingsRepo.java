package com.github.black0nion.blackonionbot.config.discord.api.repo;

import com.github.black0nion.blackonionbot.config.discord.api.container.SettingsContainer;
import net.dv8tion.jda.api.entities.ISnowflake;

public interface SettingsRepo<T extends SettingsContainer, E extends ISnowflake> {
	default T getSettings(E entity) {
		return getSettings(entity.getIdLong());
	}

	T getSettings(long identifier);
}
