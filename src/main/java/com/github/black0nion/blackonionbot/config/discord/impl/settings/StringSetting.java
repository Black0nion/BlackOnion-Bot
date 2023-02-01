package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.github.black0nion.blackonionbot.config.discord.api.settings.AbstractSetting;
import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;
import org.jetbrains.annotations.Nullable;

public class StringSetting extends AbstractSetting<String> {

	@SafeVarargs
	public StringSetting(String name, String value, @Nullable Validator<String>... validators) {
		super(name, value, s -> s, validators);
	}
}
