package com.github.black0nion.blackonionbot.config.logging;

import ch.qos.logback.core.PropertyDefinerBase;
import com.github.black0nion.blackonionbot.config.immutable.ConfigFileLoader;
import com.github.black0nion.blackonionbot.config.immutable.impl.ConfigLoaderImpl;
import com.github.black0nion.blackonionbot.misc.enums.RunMode;
import com.github.black0nion.blackonionbot.utils.Utils;

import static com.github.black0nion.blackonionbot.config.immutable.Flags.defaultValue;

public class RunModeConfig extends PropertyDefinerBase {
	@Override
	public String getPropertyValue() {
		Utils.uncheckedSupplier(ConfigFileLoader::loadConfig);

		return ConfigLoaderImpl.INSTANCE.get("run_mode", RunMode.class, defaultValue(RunMode.DEV)).name();
	}
}
