package com.github.black0nion.blackonionbot.config.discord.api;

import com.github.black0nion.blackonionbot.config.discord.api.SettingsContainer;
import com.github.black0nion.blackonionbot.config.discord.api.settings.Setting;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractSettingsContainer implements SettingsContainer {

	protected final List<Setting<?>> settings = new LinkedList<>();
	protected final long id;

	protected AbstractSettingsContainer(long id) {
		this.id = id;
	}

	@Override
	public long getIdentifier() {
		return id;
	}

	public <T extends Setting<?>> T addSetting(T setting) {
		settings.add(setting);
		return setting;
	}

	@Override
	public List<Setting<?>> getSettings() {
		return settings;
	}
}
