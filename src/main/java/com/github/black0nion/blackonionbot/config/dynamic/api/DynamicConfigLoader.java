package com.github.black0nion.blackonionbot.config.dynamic.api;

import com.github.black0nion.blackonionbot.config.immutable.api.ConfigLoader;

public interface DynamicConfigLoader extends ConfigLoader {
	void set(String name, Object value);
}
