package com.github.black0nion.blackonionbot.config;

import ch.qos.logback.core.PropertyDefinerBase;
import com.github.black0nion.blackonionbot.config.impl.ConfigLoaderImpl;
import com.github.black0nion.blackonionbot.config.impl.ConfigImpl;

public class LokiConfig extends PropertyDefinerBase {
	@Override
	public String getPropertyValue() {
		return new ConfigImpl(new ConfigLoaderImpl()).getLokiUrl();
	}
}
