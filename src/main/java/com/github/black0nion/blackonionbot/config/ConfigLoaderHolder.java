package com.github.black0nion.blackonionbot.config;

import com.github.black0nion.blackonionbot.config.common.ConfigLoader;
import com.github.black0nion.blackonionbot.misc.Holder;

/**
 * Required to set the {@link #configLoader} field before the fields of the
 * {@link com.github.black0nion.blackonionbot.config.immutable.api.Config Config} implementation are set.<br>
 * <br>
 * This works because super constructors get called <i>before</i> the fields of the subclass are set.
 */
public class ConfigLoaderHolder<T extends ConfigLoader> extends Holder<T> {

	protected final T configLoader;

	public ConfigLoaderHolder(T configLoader) {
		super(configLoader);
		this.configLoader = configLoader;
	}
}
