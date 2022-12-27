package com.github.black0nion.blackonionbot.config.logging;

import ch.qos.logback.core.PropertyDefinerBase;
import com.github.black0nion.blackonionbot.config.immutable.ConfigFileLoader;
import com.github.black0nion.blackonionbot.config.immutable.impl.ConfigLoaderImpl;
import com.github.black0nion.blackonionbot.utils.Utils;

public class LokiConfig extends PropertyDefinerBase {
	@Override
	public String getPropertyValue() {
		Utils.uncheckedSupplier(ConfigFileLoader::loadConfig);

		return ConfigLoaderImpl.INSTANCE.get("loki_url", String.class);
	}
}
