package com.github.black0nion.blackonionbot.config.mutable.api;

import com.github.black0nion.blackonionbot.config.immutable.api.ConfigLoader;

public interface MutableConfigLoader extends ConfigLoader {
	void set(String name, Object value);
}
