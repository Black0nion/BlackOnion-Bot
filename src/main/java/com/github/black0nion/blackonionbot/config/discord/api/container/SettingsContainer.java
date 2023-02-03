package com.github.black0nion.blackonionbot.config.discord.api.container;

import com.github.black0nion.blackonionbot.config.discord.api.settings.Setting;

import java.util.List;

public interface SettingsContainer {
	/**
	 * @return either the ID of the guild or the ID of the user
	 */
	long getIdentifier();

	List<? extends Setting<?>> getSettings(); // NOSONAR stfu i need this dynamicness

	<T> Setting<T> getSetting(String name);
}
