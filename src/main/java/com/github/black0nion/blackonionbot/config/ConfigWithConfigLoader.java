package com.github.black0nion.blackonionbot.config;

import com.github.black0nion.blackonionbot.config.api.ConfigLoader;

public class ConfigWithConfigLoader {

	protected final ConfigLoader configLoader;

	public ConfigWithConfigLoader(ConfigLoader configLoader) {
		this.configLoader = configLoader;
	}
}
