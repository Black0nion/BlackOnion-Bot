package com.github.black0nion.blackonionbot.utils.config;

import ch.qos.logback.core.PropertyDefinerBase;

public class LokiConfig extends PropertyDefinerBase {
	@Override
	public String getPropertyValue() {
		return Config.getInstance().getLokiUrl();
	}
}