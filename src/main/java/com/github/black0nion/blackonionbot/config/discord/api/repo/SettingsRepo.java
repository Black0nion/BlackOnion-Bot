package com.github.black0nion.blackonionbot.config.discord.api.repo;

import com.github.black0nion.blackonionbot.config.discord.api.container.SettingsContainer;

public interface SettingsRepo<T extends SettingsContainer> {
	T getSettings(long identifier);
}
