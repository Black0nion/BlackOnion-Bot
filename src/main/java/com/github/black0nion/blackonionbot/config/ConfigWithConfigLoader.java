package com.github.black0nion.blackonionbot.config;

import com.github.black0nion.blackonionbot.config.api.ConfigLoader;

/**
 * Required to set the {@link #configLoader} field before the fields of the
 * {@link com.github.black0nion.blackonionbot.config.api.Config Config} implementation are set.<br>
 * <br>
 * This works because super constructors get called <i>before</i> the fields of the subclass are set.
 */
public class ConfigWithConfigLoader {

	protected final ConfigLoader configLoader;

	public ConfigWithConfigLoader(ConfigLoader configLoader) {
		this.configLoader = configLoader;
	}
}
