package com.github.black0nion.blackonionbot.config.discord.api;

public interface SettingsRepo<T extends SettingsContainer> {
	T getSettings(long identifier);
}
